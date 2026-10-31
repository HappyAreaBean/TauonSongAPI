package cc.happyareabean.tauonsongapi;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class GSASongData {

    private String status;
    private String artist;
    private String title;
    private String album;
    private String albumArt;
    private String song;
}
