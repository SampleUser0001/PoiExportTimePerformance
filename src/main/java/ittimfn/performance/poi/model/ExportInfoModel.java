package ittimfn.performance.poi.model;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
    "column",
    "line",
    "exportCsvPath",
    "time"
})
@Data
public class ExportInfoModel {
    @JsonProperty
    private int column;
    @JsonProperty
    private int line;
    @JsonProperty
    private String exportCsvPath;
    @JsonProperty
    private long time;

    @JsonIgnore
    public int currentLine;
}
