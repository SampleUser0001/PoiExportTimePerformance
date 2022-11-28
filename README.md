# PoiExportTimePerformance
Poiの出力時間を計測する。

## 実行

``` bash
column=10
line=500
mvn clean compile exec:java -Dexec.mainClass="ittimfn.performance.poi.App" -Dexec.args="'${column}' '${line}'"
```