#!/bin/bash
#
# Copyright (c) 2015, Providence Authors
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

GW="$(which generate_workspace)"

if [[ ! -f ${GW} ]]; then
    echo "you need to build the generate_workspace script form bazel workspace"
    echo "and make it available via the 'generate_workspace' command."
    echo
    echo "# cd \${BAZEL_WORKSPACE}"
    echo "# bazel build //src/tools/generate_workspace:generate_workspace_deploy.jar"
    exit 1
fi

TMP_file=$(mktemp -t 'backend-servlet.XXXXXXXXXX')

POMS=( $(find | grep '/pom.xml' | sed -e 's:/pom.xml$::' -e 's:^./:-m :') )

echo "-- log file ${TMP_file}"
echo "## generate_workspace ${POMS[@]}"
generate_workspace ${POMS[@]} 1> ${TMP_file} 2>&1 || exit 1
echo "-- done"

WS=$(cat ${TMP_file} | tail -n 2 | head -n 1)
BUILD=$(cat ${TMP_file} | tail -n 1)

echo "-- writing WORKSPACE"
cat third-party/common.WORKSPACE > WORKSPACE
cat ${WS} >> WORKSPACE
echo "-- writing third-party/BUILD"
cat third-party/common.BUILD > third-party/BUILD
cat ${BUILD} | sed 's:name = \"[^_]*_\(.*\)\":name = \"\1\":' \
    >> third-party/BUILD

exit 0
