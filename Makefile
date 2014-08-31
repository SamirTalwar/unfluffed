FAYE_DIR=vendor/faye
FAYE_OUTPUT_DIR=src/generated/static/faye

application: faye
	mvn package

faye: build-faye-browser copy-faye-browser

build-faye-browser:
	cd $(FAYE_DIR) && \
	npm install && \
	npm run-script build && \
	cd -

copy-faye-browser:
	mkdir -p $$(dirname $(FAYE_OUTPUT_DIR)) && \
	([[ -e $(FAYE_OUTPUT_DIR) ]] && rm -f $(FAYE_OUTPUT_DIR) || :) && \
	cp -R $(FAYE_DIR)/build/browser $(FAYE_OUTPUT_DIR)
