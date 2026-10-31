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
        "status",
        "inc",
        "shuffle",
        "repeat",
        "progress",
        "auto_stop",
        "volume",
        "playlist",
        "playlist_length",
        "id",
        "title",
        "artist",
        "album",
        "track",
        "position",
        "album_id"
})
@Getter
public class Status {

    @JsonProperty("status")
    private String status;
    @JsonProperty("inc")
    private Integer inc;
    @JsonProperty("shuffle")
    private Boolean shuffle;
    @JsonProperty("album_shuffle")
    private Boolean album_shuffle;
    @JsonProperty("repeat")
    private Boolean repeat;
    @JsonProperty("progress")
    private Integer progress;
    @JsonProperty("auto_stop")
    private Boolean autoStop;
    @JsonProperty("volume")
    private Integer volume;
    @JsonProperty("playlist")
    private String playlist;
    @JsonProperty("playlist_length")
    private Integer playlistLength;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("artist")
    private String artist;
    @JsonProperty("album")
    private String album;
    @JsonProperty("track")
    private Track track;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("album_id")
    private Integer albumId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
