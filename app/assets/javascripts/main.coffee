class GarageBuddy
  constructor: ->
    console.log("(GarageBuddy) Loaded scripts for 1.0.0-alpha8")

class GarageBuddy.BarcodeReader
  @config:
    debug:
      drawBoundingBox: true
      drawScanline: true
      showFrequency: false
      showPattern: false
    halfSample: true
    inputStream:
      type: "ImageStream"
      constraints:
        facingMode: 'environment'
        width: 640
    locate: true
    multiple: false
    patchSize: 'medium'
    readers: ['code_128_reader']

  constructor: ({@el, @id}) ->
    @inputEl = @el
    @inputEl.on 'change', @inputChanged

  inputChanged: (e) =>
    target = e.currentTarget
    if target.files && target.files.length
      @decode URL.createObjectURL(target.files[0])

  decode: (src) =>
    config = $.extend @config, src: src
    Quagga.decodeSingle(config, @executeBarcode)

  executeBarcode: (result) =>
    code = result.codeResult?.code
    window.location.href = "/transactions/#{@id}/items/#{code}/add"

class GarageBuddy.SalesCharts
  constructor: ({@id, @el}) ->
    $.get("/sales/#{@id}/stats")
      .done (data) =>
        data = @formatHighchartsData(data)
        for element in @el.find('.chart')
          @generateTimeSeriesChart($(element), data)

  formatHighchartsData: (data) ->
    formattedData = {}
    for key, value of data
      value = _.pairs(value).map (arr) -> [new Date(arr[0]).getTime(), arr[1]]
      formattedData[key] = value
    return formattedData

  generateTimeSeriesChart: (element, formattedData) ->
    element.highcharts
      chart:
        type: 'area'
        spacing: [25, 0, 25, 0]
      title: null
      xAxis:
        type: 'datetime'
        tickAmount: 7
        title: enabled: false
        labels:
          format: '{value:%b %d}'
      yAxis:
        title:
          text: element.data('chart-y-axis-title')
        maxPadding: 0.5
      tooltip:
        shared: true
        valuePrefix: if element.data('chart-unit-prefix') then "#{element.data('chart-unit-prefix')}" else null
        valueSuffix: if element.data('chart-unit-suffix') then " #{element.data('chart-unit-suffix')}" else null
      plotOptions:
        area:
          lineColor: '#666666'
          lineWidth: 1
          marker:
            enabled: false
            lineWidth: 1
            lineColor: '#666666'
          pointInterval: 6
          turboThreshold: 120
      legend:
        align: 'left'
        backgroundColor: 'rgba(255,255,255,0.8)'
        borderRadius: 0
        borderWidth: 0
        floating: true
        itemMarginBottom: 10
        layout: 'vertical'
        symbolHeight: 15
        symbolRadius: 100
        symbolWidth: 15
        verticalAlign: 'top'
        x: 75
        y: 0
      credits: enabled: false
      series: [
        {name: "#{element.data('chart-series-name')}", stacking: 'normal', data: formattedData[element.data('chart-key')]}
      ]


window.GarageBuddy = GarageBuddy
$ -> new GarageBuddy()