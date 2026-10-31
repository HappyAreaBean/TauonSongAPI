package cc.happyareabean.tauonsongapi.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"title",
"artist",
"album",
"album_artist",
"duration",
"id",
"position",
"path",
"album_id",
"has_lyrics",
"track_number",
"can_download"
})
@Getter
public class Track {

    @JsonProperty("title")
    private String title;
    @JsonProperty("artist")
    private String artist;
    @JsonProperty("album")
    private String album;
    @JsonProperty("album_artist")
    private String albumArtist;
    @JsonProperty("duration")
    private Integer duration;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("path")
    private String path;
    @JsonProperty("album_id")
    private Integer albumId;
    @JsonProperty("has_lyrics")
    private Boolean hasLyrics;
    @JsonProperty("track_number")
    private String trackNumber;
    @JsonProperty("can_download")
    private Boolean canDownload;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}