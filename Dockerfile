FROM alpine:3.9

LABEL maintainer="nikitavbv@gmail.com"

RUN mkdir /app
WORKDIR /app
EXPOSE 80

RUN apk --update add bash curl unzip zip openjdk11 nodejs nodejs-npm xvfb firefox-esr ttf-dejavu ttf-liberation font-noto

RUN mkdir /geckodriver && \
    cd /geckodriver && \
    wget https://github.com/mozilla/geckodriver/releases/download/v0.24.0/geckodriver-v0.24.0-linux64.tar.gz && \
    tar -xvzf geckodriver* && \
    chmod +x geckodriver

RUN curl -s "https://get.sdkman.io" | bash \
    && bash -c "source \"/root/.sdkman/bin/sdkman-init.sh\" && sdk install gradle"

ADD src /app/src
ADD frontend/ /app/frontend
ADD build.gradle /app/build.gradle
ADD src/main/resources/application.properties.docker /app/src/main/resources/application.properties

RUN cd /app/frontend \
    && mkdir /app/src/main/resources/static \
    && npm install && npm link @angular/cli \
    && ng build --prod \
    && mv /app/frontend/dist/frontend/* /app/src/main/resources/static \
    && cd .. && rm frontend -rf
RUN bash -c "source \"/root/.sdkman/bin/sdkman-init.sh\" && gradle assemble"

CMD ["java", "-Xmx64M", "-Xms64M", "-jar", "/app/build/libs/app-0.0.1-SNAPSHOT.jar"]
