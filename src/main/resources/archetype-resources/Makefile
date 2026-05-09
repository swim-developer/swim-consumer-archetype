#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
REGISTRY ?= quay.io/masales
TAG      ?= latest
IMAGE    := ${artifactId}
PLATFORMS := linux/amd64,linux/arm64
MVN_NATIVE := -Dnative -DskipTests \
              -Dquarkus.native.container-build=true \
              -Dquarkus.native.container-runtime=podman
SONAR_URL   ?= http://localhost:9000
SONAR_TOKEN ?=

PARENT_DIR  := ${symbol_dollar}(abspath ${symbol_dollar}(dir ${symbol_dollar}(abspath ${symbol_dollar}(lastword ${symbol_dollar}(MAKEFILE_LIST))))/..)
GITHUB_SSH  := git@github.com:swim-developer

# modelArtifactId was set at archetype generation time (-DmodelArtifactId=<value>).
# Examples: swim-aixm-model | swim-fixm-model-ed254 | swim-fixm-ffice-model
SYNC_DEPS := swim-developer-root ${modelArtifactId} swim-developer-framework swim-developer-extensions

.PHONY: help sync pull pull-deps install-deps deps build test jvm \
        native-amd64 native-arm64 manifest push native \
        sonar sonar-up sonar-down security-deps security-image

help:
	@echo ""
	@echo "  ${artifactId} — available targets"
	@echo "  ─────────────────────────────────────────────────────────"
	@echo ""
	@echo "  Local dev:"
	@echo "    sync               Full setup: pull + pull-deps + install-deps"
	@echo "    pull               Pull this project from remote"
	@echo "    pull-deps          Clone missing deps + pull existing ones in ${symbol_dollar}(PARENT_DIR)"
	@echo "    install-deps       Install all deps into local Maven repository"
	@echo "    build              Compile + package JAR (skips tests)"
	@echo "    test               Unit + integration tests (Testcontainers)"
	@echo "    deps               Show which sibling repos must be installed first"
	@echo ""
	@echo "  Container images (multi-arch):"
	@echo "    jvm                JVM image — build + push (run on any arch)"
	@echo "    native-amd64       Native amd64 image — build + push  (run on amd64)"
	@echo "    native-arm64       Native arm64 image — build + push  (run on arm64)"
	@echo "    manifest           Create multi-arch manifest from registry images"
	@echo "    push               Push manifest to registry"
	@echo "    native             Full native sequence (amd64 + arm64 + manifest + push)"
	@echo ""
	@echo "  Analysis:"
	@echo "    sonar-up           Start SonarQube at http://localhost:9000"
	@echo "    sonar              Run SonarQube analysis (requires SonarQube running)"
	@echo "    sonar-down         Stop SonarQube"
	@echo "    security-deps      OWASP Dependency-Check for application dependencies"
	@echo "    security-image     Trivy vulnerability scan for container image"
	@echo ""
	@echo "  Variables: REGISTRY=${symbol_dollar}(REGISTRY)  TAG=${symbol_dollar}(TAG)"

sync: pull pull-deps install-deps

pull:
	@echo ""
	@echo "  ── Pull this project ────────────────────────────────────────"
	@git pull --ff-only
	@echo ""

pull-deps:
	@echo ""
	@echo "  ── Ensure sibling dependencies in ${symbol_dollar}(PARENT_DIR) ─────────────"
	@for repo in ${symbol_dollar}(SYNC_DEPS); do \
	  dir="${symbol_dollar}(PARENT_DIR)/${symbol_dollar}${symbol_dollar}repo"; \
	  if [ ! -d "${symbol_dollar}${symbol_dollar}dir" ]; then \
	    echo "  CLONE   ${symbol_dollar}${symbol_dollar}repo"; \
	    git clone "${symbol_dollar}(GITHUB_SSH)/${symbol_dollar}${symbol_dollar}repo.git" "${symbol_dollar}${symbol_dollar}dir" --quiet; \
	  else \
	    printf "  PULL    ${symbol_dollar}${symbol_dollar}repo ... "; \
	    git -C "${symbol_dollar}${symbol_dollar}dir" pull --ff-only --quiet 2>&1 && echo "ok" || echo "skipped (local changes or detached HEAD)"; \
	  fi; \
	done
	@echo ""

install-deps:
	@echo ""
	@echo "  ── Install dependencies into local Maven repository ─────────"
	@for repo in ${symbol_dollar}(SYNC_DEPS); do \
	  dir="${symbol_dollar}(PARENT_DIR)/${symbol_dollar}${symbol_dollar}repo"; \
	  if [ ! -d "${symbol_dollar}${symbol_dollar}dir" ]; then \
	    echo "  SKIP    ${symbol_dollar}${symbol_dollar}repo (not found — run: make pull-deps)"; \
	    continue; \
	  fi; \
	  mvn_cmd="mvn"; \
	  [ -f "${symbol_dollar}${symbol_dollar}dir/mvnw" ] && mvn_cmd="${symbol_dollar}${symbol_dollar}dir/mvnw"; \
	  if [ "${symbol_dollar}${symbol_dollar}repo" = "swim-developer-root" ]; then \
	    args="install -N -DskipTests -q"; \
	  else \
	    args="clean install -DskipTests -q"; \
	  fi; \
	  printf "  INSTALL ${symbol_dollar}${symbol_dollar}repo ... "; \
	  "${symbol_dollar}${symbol_dollar}mvn_cmd" -f "${symbol_dollar}${symbol_dollar}dir/pom.xml" ${symbol_dollar}${symbol_dollar}args && echo "ok" || { echo "FAIL"; exit 1; }; \
	done
	@echo ""
	@echo "  Done. Run: make build"
	@echo ""

deps:
	@echo ""
	@echo "  Required sibling repos — install to local Maven repo first:"
	@echo ""
	@echo "    git clone https://github.com/swim-developer/swim-developer-root"
	@echo "    cd swim-developer-root && ./mvnw install -N -DskipTests"
	@echo ""
	@echo "    git clone https://github.com/swim-developer/${modelArtifactId}"
	@echo "    cd ${modelArtifactId} && ./mvnw clean install -DskipTests"
	@echo ""
	@echo "    git clone https://github.com/swim-developer/swim-developer-framework"
	@echo "    cd swim-developer-framework && ./mvnw clean install -DskipTests"
	@echo ""
	@echo "    git clone https://github.com/swim-developer/swim-developer-extensions"
	@echo "    cd swim-developer-extensions && ./mvnw clean install -DskipTests"
	@echo ""

build:
	./mvnw clean package -DskipTests

test:
	./mvnw verify -DskipITs=false

jvm:
	-podman rmi  ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) 2>/dev/null
	-podman manifest rm ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) 2>/dev/null
	podman buildx build --platform ${symbol_dollar}(PLATFORMS) \
		--manifest ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) \
		-f src/main/docker/Dockerfile.jvm .
	podman manifest push --all ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) \
		docker://${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)

native-amd64:
	./mvnw clean package ${symbol_dollar}(MVN_NATIVE) \
		-Dquarkus.native.container-runtime-options="--platform=linux/amd64"
	podman build --platform linux/amd64 \
		-t ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-amd64 \
		-f src/main/docker/Dockerfile.native .
	podman push ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-amd64

native-arm64:
	./mvnw clean package ${symbol_dollar}(MVN_NATIVE) \
		-Dquarkus.native.container-runtime-options="--platform=linux/arm64"
	podman build --platform linux/arm64 \
		-t ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-arm64 \
		-f src/main/docker/Dockerfile.native .
	podman push ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-arm64

manifest:
	-podman manifest rm ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) 2>/dev/null
	podman manifest create ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) \
		${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-amd64 \
		${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)-arm64

push:
	podman manifest push --all ${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG) \
		docker://${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)

native: native-amd64 native-arm64 manifest push

sonar-up:
	@echo "Starting SonarQube..."
	@podman rm -f ${symbol_dollar}(IMAGE)-sonarqube 2>/dev/null || true
	podman run -d \
		--name ${symbol_dollar}(IMAGE)-sonarqube \
		-e SONAR_FORCEAUTHENTICATION=false \
		-e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
		-p 9000:9000 \
		docker.io/library/sonarqube:25.4.0.105899-community
	@echo "Waiting for SonarQube to be ready..."
	@until curl -sf http://localhost:9000/api/system/status | grep -q '"status":"UP"'; do sleep 3; done
	@echo "Granting anonymous scan permission..."
	@curl -sf -u admin:admin -X POST "http://localhost:9000/api/permissions/add_group" \
		-d "permission=scan&groupName=Anyone" > /dev/null || true
	@curl -sf -u admin:admin -X POST "http://localhost:9000/api/permissions/add_group" \
		-d "permission=provisioning&groupName=Anyone" > /dev/null || true
	@echo "SonarQube is up at http://localhost:9000"

sonar-down:
	podman rm -f ${symbol_dollar}(IMAGE)-sonarqube

sonar:
	./mvnw clean verify sonar:sonar \
		-DskipITs=false \
		-Dsonar.host.url=${symbol_dollar}(SONAR_URL) \
		${symbol_dollar}(if ${symbol_dollar}(SONAR_TOKEN),-Dsonar.login=${symbol_dollar}(SONAR_TOKEN),) \
		-Dsonar.projectKey=${artifactId} \
		-Dsonar.projectName=${artifactId}

security-deps:
	./mvnw org.owasp:dependency-check-maven:aggregate \
		-DfailBuildOnCVSS=7 -Dformats=HTML,JSON -DskipTests \
		-DsuppressionFile=owasp-suppressions.xml
	@echo "Report: target/dependency-check-report.html"

security-image:
	trivy image --exit-code 1 --severity HIGH,CRITICAL \
		${symbol_dollar}(REGISTRY)/${symbol_dollar}(IMAGE):${symbol_dollar}(TAG)
