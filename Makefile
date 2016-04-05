INSTALL_DIR="${HOME}/.apps/lib/jars"
BIN_DIR="${HOME}/.apps/bin"

site:
	mvn clean install site
	bash src/scripts/shell/combine-sites.sh

.PHONY: *
