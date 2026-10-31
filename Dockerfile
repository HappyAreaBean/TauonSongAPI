FROM bellsoft/liberica-runtime-container:jre-21-glibc
WORKDIR /app

COPY build/libs/TauonSongAPI.jar ./TauonSongAPI.jar

ENV STATIC_FILES_PATH=/app/html
ENV APP_PORT=7000

EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/app/TauonSongAPI.jar"]
