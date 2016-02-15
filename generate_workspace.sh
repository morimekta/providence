#!/bin/bash
#
# Copyright (c) 2016, Stein Eldar Johnsen
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

location="${PWD}"
bazel=$(which bazel)
if [[ -z "${bazel}" ]]
then
    echo "you need install bazel and make it available in the path before"
    echo "running the ./generate_workspace.sh command. The bazel binary must"
    echo "also reside within the bazel workspace (may be symlinked)."
    exit 1
fi

if [[ -s ${bazel} ]]
then
    bazel=$(readlink -f $bazel)
fi

bazel_workspace=${bazel%/bazel}
while [[ ! -f "${bazel_workspace}/WORKSPACE" ]]
do
    bazel_workspace=${bazel_workspace%/*}
    if [[ -z "$bazel_workspace" ]]
    then
        echo "ERROR: The bazel binary must be located within the bazel workspace."
        echo
        echo "run # ./compile.sh compile"
        echo
        echo "from within your bazel workspace, and make sure it is available via the"
        echo "path. Symlinking the binary to a /bin folder is fine."
        exit 1
    fi
done

TMP_file=$(mktemp -t 'generate_workspace.XXXXXXXXXX')
POMS=( $(find | grep -v '^\./pom.xml' | grep '/pom.xml' | sed -e 's:/pom.xml$::' -e "s:^./:-m ${location}/:") )

echo "-- log file ${TMP_file}"
echo "## generate_workspace ${POMS[@]}"

cd ${bazel_workspace}

bazel run //src/tools/generate_workspace -- ${POMS[@]} 1> ${TMP_file} 2>&1 || exit 1

echo "-- done"
echo

WS=$(cat ${TMP_file} | tail -n 2 | head -n 1)
BUILD=$(cat ${TMP_file} | tail -n 1)

cd ${location}

if [[ ! -d third-party ]]
then
    mkdir third-party
fi

echo "-- writing WORKSPACE"
if [[ -f third-party/common.WORKSPACE ]]
then
    cat third-party/common.WORKSPACE                         > WORKSPACE
    echo                                                    >> WORKSPACE
    echo "# --- generated dependencies below this line ---" >> WORKSPACE
    echo                                                    >> WORKSPACE
else
    echo "# --- create a third-party/common.WORKSPACE file to add non-managed" > WORKSPACE
    echo "# --- third-party dependencies to this file."                       >> WORKSPACE
    echo                                                                      >> WORKSPACE
fi
cat ${WS} >> WORKSPACE

echo "-- writing third-party/BUILD"
if [[ -f third-party/common.BUILD ]]
then
    cat third-party/common.BUILD                             > third-party/BUILD
    echo                                                    >> third-party/BUILD
    echo "# --- generated dependencies below this line ---" >> third-party/BUILD
    echo                                                    >> third-party/BUILD
else
    echo "# --- create a third-party/common.BUILD file to add non-managed" > third-party/BUILD
    echo "# --- third-party dependencies to this file."                   >> third-party/BUILD
    echo                                                                  >> third-party/BUILD
fi
cat ${BUILD} >> third-party/BUILD

exit 0
