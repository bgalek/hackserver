FROM artifactory.allegrogroup.com/ecosystem/alpine-bamboo-jdk:jdk10.0.1

RUN \
  echo "http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories \
  && echo "http://dl-cdn.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories \
  && echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories \
  && apk --no-cache  update \
  && apk --no-cache  upgrade \
  && apk add chromium chromium-chromedriver xvfb

ENV CHROME_BIN /usr/bin/chromium-browser
ENV LIGHTHOUSE_CHROMIUM_PATH /usr/bin/chromium-browser