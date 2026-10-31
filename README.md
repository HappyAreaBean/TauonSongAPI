# TauonSongAPI

TauonSongAPI is a Javalin application outputting track info by using Tauon's remote control API.

# Prerequisites

1. JDK 21+

The docker usage is not ready yet, unless you figured out how to read Tauon API in your machine 🤷

please let me know if you figured that out.

# Usage

1. Download the latest jar in [releases](https://github.com/HappyAreaBean/TauonSongAPI/releases)
2. Set up the required environment (See below)
3. Launch it: `java -jar TauonSongAPI.jar`

# Environment & Configuration

Currently, there are no config file and config are only able to use with environment variables.

### (Required) `TAUON_API_URL`

Example: `localhost:7814`

The Tauon remote control API url.

### (Optional) `APP_URL`

Example: `https://myapi.com/`

The host name your app be running at.
Mainly using it for returning a proper album art url, otherwise the hostname is automatically resolve by default.

### (Optional) `APP_PORT`

Default: `7000`

Port.

### (Optional) `STATIC_FILES_PATH`

Default: `<workDir>/html`

The static files folder, it will be served on `/static` endpoints. Mainly for thing like obs overlay, since obs have
issues with CORS.

For example [my obs now playing overlay](https://github.com/HappyAreaBean/obs-now-playing-tauon). (Haven't updated for
TauonSongAPI tho will do it later.)

# Endpoints

The API listens on `APP_PORT` (default: `7000`). Playback endpoints fetch the current status from the Tauon API
configured by `TAUON_API_URL`.

| Method | Endpoint              | Description                                                   |
|--------|-----------------------|---------------------------------------------------------------|
| `GET`  | `/get/song`           | Current track as `artist - title`.                            |
| `GET`  | `/get/title`          | Current track title.                                          |
| `GET`  | `/get/artist`         | Current artist.                                               |
| `GET`  | `/get/album`          | Current album.                                                |
| `GET`  | `/get/status`         | Playback status and track information as plain text.          |
| `GET`  | `/get/full`           | Same response behavior as `/get/status`.                      |
| `GET`  | `/get/album-art`      | Redirects to the album-art endpoint for the current track.    |
| `GET`  | `/get/album-art/{id}` | Returns album-art image bytes for the specified track ID.     |
| `GET`  | `/static/{fileName}`  | Serves a file from `STATIC_FILES_PATH`, or `./html` if unset. |

### Response formats

Use `?format=json` or `?format=plain` with `/get/song`, `/get/title`, `/get/artist`, `/get/album`, and `/get/album-art`.

For example: `GET /get/song?format=json`. Without a format, these endpoints return plain text,
except `/get/album-art`, which redirects to the image.

`/get/status` and `/get/full` return playback details as plain text.

If playback is stopped or paused, they return `No song is playing as the player is stopped.` 

If track details aren’t available, they return `No song is playing.` 

Use `?format=json` to get these details as JSON; `format=plain` isn’t
supported for these endpoints.

Although the server also accepts a `{dataType}` path segment, it uses the `format` query parameter to choose the
response format.

### Errors

Invalid endpoint types or format values return `400`. Upstream failures or parsing errors return an error response;
missing static files return `404`.

<sup>btw the Endpoints, Response formats and Errors part are written by AI. I'm lazy to do all that.</sup>