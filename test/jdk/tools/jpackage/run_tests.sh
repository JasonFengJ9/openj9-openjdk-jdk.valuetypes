#!/bin/bash

#
# Script to run jpackage tests.
#


# Fail fast
set -e; set -o pipefail;


workdir=/tmp/jpackage_jtreg_testing
jtreg_jar=$workdir/jtreg/lib/jtreg.jar
jpackage_test_selector=test/jdk/tools/jpackage


find_packaging_tests ()
{
  (cd "$open_jdk_with_jpackage_jtreg_tests" && \
    find "$jpackage_test_selector/$1" -type f -name '*.java' \
    | xargs grep -E -l '@key[[:space:]]+jpackagePlatformPackage')
}


find_all_packaging_tests ()
{
  find_packaging_tests share
  case "$(uname -s)" in
    Darwin)
      find_packaging_tests macosx;;
    Linux)
      find_packaging_tests linux;;
    CYGWIN*|MINGW32*|MSYS*)
      find_packaging_tests windows;;
    *)
      fatal Failed to detect OS type;;
  esac
}


help_usage ()
{
  echo "Usage: `basename $0` [options] [test_names]"
  echo "Options:"
  echo "  -h              - print this message"
  echo "  -v              - verbose output"
  echo "  -c              - keep jtreg cache"
  echo "  -a              - run all, not only SQE tests"
  echo "  -d              - dry run. Print jtreg command line, but don't execute it"
  echo "  -t <jdk>        - path to JDK to be tested [ mandatory ]"
  echo "  -j <openjdk>    - path to local copy of openjdk repo with jpackage jtreg tests"
  echo "                    Optional, default is openjdk repo where this script resides"
  echo "  -o <outputdir>  - path to folder where to copy artifacts for testing."
  echo "                    Optional, default is the current directory."
  echo '  -r <runtimedir> - value for `jpackage.test.runtime-image` property.'
  echo "                    Optional, for jtreg tests debug purposes only."
  echo '  -l <logfile>    - value for `jpackage.test.logfile` property.'
  echo "                    Optional, for jtreg tests debug purposes only."
  echo "  -m <mode>       - mode to run jtreg tests."
  echo '                    Should be one of `create`, `update`, `verify-install` or `verify-uninstall`.'
  echo '                    Optional, default mode is `update`.'
  echo '                    - `create`'
  echo '                      Remove all package bundles from the output directory before running jtreg tests.'
  echo '                    - `update`'
  echo '                      Run jtreg tests and overrite existing package bundles in the output directory.'
  echo '                    - `verify-install`'
  echo '                      Verify installed packages created with the previous run of the script.'
  echo '                    - `verify-uninstall`'
  echo '                      Verify packages created with the previous run of the script were uninstalled cleanly.'
  echo '                    - `print-default-tests`'
  echo '                      Print default list of packaging tests and exit.'
}

error ()
{
  echo "$@" > /dev/stderr
}

fatal ()
{
  error "$@"
  exit 1
}

fatal_with_help_usage ()
{
  error "$@"
  help_usage
  exit 1
}

if command -v cygpath &> /dev/null; then
to_native_path ()
{
  cygpath -m "$@"
}
else
to_native_path ()
{
  echo "$@"
}
fi

exec_command ()
{
  if [ -n "$dry_run" ]; then
    echo "$@"
  else
    eval "$@"
  fi
}


# Path to JDK to be tested.
test_jdk=

# Path to local copy of open jdk repo with jpackage jtreg tests
# hg clone http://hg.openjdk.java.net/jdk/sandbox
# cd sandbox; hg update -r JDK-8200758-branch
open_jdk_with_jpackage_jtreg_tests=$(dirname $0)/../../../../

# Directory where to save artifacts for testing.
output_dir=$PWD

# Script and jtreg debug.
verbose=
jtreg_verbose="-verbose:fail,error,summary"

keep_jtreg_cache=

# Mode in which to run jtreg tests
mode=update

# jtreg extra arguments
declare -a jtreg_args

# Run all tests
run_all_tests=

mapfile -t tests < <(find_all_packaging_tests)

while getopts "vahdct:j:o:r:m:l:" argname; do
  case "$argname" in
    v) verbose=yes;;
    a) run_all_tests=yes;;
    d) dry_run=yes;;
    c) keep_jtreg_cache=yes;;
    t) test_jdk="$OPTARG";;
    j) open_jdk_with_jpackage_jtreg_tests="$OPTARG";;
    o) output_dir="$OPTARG";;
    r) runtime_dir="$OPTARG";;
    l) logfile="$OPTARG";;
    m) mode="$OPTARG";;
    h) help_usage; exit 0;;
    ?) help_usage; exit 1;;
  esac
done
shift $(( OPTIND - 1 ))

[ -z "$verbose" ] || { set -x; jtreg_verbose=-va; }

if [ -z "$open_jdk_with_jpackage_jtreg_tests" ]; then
  fatal_with_help_usage "Path to openjdk repo with jpackage jtreg tests not specified"
fi

if [ "$mode" = "print-default-tests" ]; then
  exec_command for t in ${tests[@]}";" do echo '$t;' done
  exit
fi

if [ -z "$test_jdk" ]; then
  fatal_with_help_usage Path to test JDK not specified
fi

if [ -z "$JAVA_HOME" ]; then
  echo JAVA_HOME environment variable not set, will use java from test JDK [$test_jdk] to run jtreg
  JAVA_HOME="$test_jdk"
fi
if [ ! -e "$JAVA_HOME/bin/java" ]; then
  fatal JAVA_HOME variable is set to [$JAVA_HOME] value, but $JAVA_HOME/bin/java not found.
fi

if [ -n "$runtime_dir" ]; then
  if [ ! -d "$runtime_dir" ]; then
    fatal 'Value of `-r` option is set to non-existing directory'.
  fi
  jtreg_args+=("-Djpackage.test.runtime-image=$(to_native_path "$(cd "$runtime_dir" && pwd)")")
fi

if [ -n "$logfile" ]; then
  if [ ! -d "$(dirname "$logfile")" ]; then
    fatal 'Value of `-l` option specified a file in non-existing directory'.
  fi
  logfile="$(cd "$(dirname "$logfile")" && pwd)/$(basename "$logfile")"
  jtreg_args+=("-Djpackage.test.logfile=$(to_native_path "$logfile")")
fi

if [ "$mode" = create ]; then
  true
elif [ "$mode" = update ]; then
  true
elif [ "$mode" = verify-install ]; then
  jtreg_args+=("-Djpackage.test.action=$mode")
elif [ "$mode" = verify-uninstall ]; then
  jtreg_args+=("-Djpackage.test.action=$mode")
else
  fatal_with_help_usage 'Invalid value of -m option:' [$mode]
fi

if [ -z "$run_all_tests" ]; then
  jtreg_args+=(-Djpackage.test.SQETest=yes)
fi

# All remaining command line arguments are tests to run that should override the defaults
[ $# -eq 0 ] || tests=($@)


installJtreg ()
{
  # Install jtreg if missing
  if [ ! -f "$jtreg_jar" ]; then
    exec_command mkdir -p "$workdir"
    # TODO - restore code to download or copy jtreg.jar
    # to $workdir/jtreg/lib/jtreg.jar
    fatal "ERROR: All Tests Disabled until locating jtreg.jar implemented."
  fi
}


preRun ()
{
  local xargs_args=(-t --no-run-if-empty rm)
  if [ -n "$dry_run" ]; then
    xargs_args=(--no-run-if-empty echo rm)
  fi

  if [ ! -d "$output_dir" ]; then
    exec_command mkdir -p "$output_dir"
  fi
  [ ! -d "$output_dir" ] || output_dir=$(cd "$output_dir" && pwd)

  # Clean output directory
  [ "$mode" != "create" ] || find $output_dir -maxdepth 1 -type f -name '*.exe' -or -name '*.msi' -or -name '*.rpm' -or -name '*.deb' | xargs "${xargs_args[@]}"
}


run ()
{
  local jtreg_cmdline=(\
    $JAVA_HOME/bin/java -jar $(to_native_path "$jtreg_jar") \
    "-Djpackage.test.output=$(to_native_path "$output_dir")" \
    "${jtreg_args[@]}" \
    -nr \
    "$jtreg_verbose" \
    -retain:all \
    -automatic \
    -ignore:run \
    -testjdk:"$(to_native_path $test_jdk)" \
    -dir:"$(to_native_path $open_jdk_with_jpackage_jtreg_tests)" \
    -reportDir:"$(to_native_path $workdir/run/results)" \
    -workDir:"$(to_native_path $workdir/run/support)" \
    "${tests[@]}" \
  )

  # Clear previous results
  [ -n "$keep_jtreg_cache" ] || exec_command rm -rf "$workdir"/run

  # Run jpackage jtreg tests to create artifacts for testing
  exec_command ${jtreg_cmdline[@]}
}


installJtreg
preRun
run
