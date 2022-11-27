# PoiExportTimePerformance
Poiの出力時間を計測する。

## 実行

``` bash
column=100
line=50000
mvn clean compile exec:java -Dexec.mainClass="ittimfn.performance.poi.App" -Dexec.args="'${column}' '${line}'"
```