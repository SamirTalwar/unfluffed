GENERATED_OUTPUT_DIR=src/generated
FAYE_DIR=vendor/faye
FAYE_OUTPUT_DIR=$(GENERATED_OUTPUT_DIR)/com/codurance/unfluffed/static/faye

application: faye unfluffed

unfluffed:
	mvn package

faye: build-faye-browser $(FAYE_OUTPUT_DIR)

build-faye-browser:
	cd $(FAYE_DIR) && \
	npm install && \
	npm run-script build && \
	cd -

$(FAYE_OUTPUT_DIR):
	mkdir -p $$(dirname $(FAYE_OUTPUT_DIR)) && \
	([[ -e $(FAYE_OUTPUT_DIR) ]] && rm -r $(FAYE_OUTPUT_DIR) || :) && \
	cp -R $(FAYE_DIR)/build/browser $(FAYE_OUTPUT_DIR)

clean:
	rm -rf $(GENERATED_OUTPUT_DIR)
