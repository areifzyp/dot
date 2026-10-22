import org.kde.kirigami as Kirigami
import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    Layout.preferredWidth: Kirigami.Units.gridUnit * 14
    Layout.preferredHeight: Kirigami.Units.gridUnit * 8

    property string cityName: "Jakarta"
    property real latitude: -6.2088
    property real longitude: 106.8456
    property string temperature: "--°C"
    property string condition: "Loading..."
    property string updatedAt: "--:--"

    FontLoader {
        id: dotoFont
        source: "../fonts/Doto.ttf"
    }

    Component.onCompleted: {
        fetchWeather()
    }

    Timer {
        interval: 1800000 // 30 minutes
        running: true
        repeat: true
        onTriggered: fetchWeather()
    }

    function fetchWeather() {
        var xhr = new XMLHttpRequest()
        var url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,weather_code"
        xhr.open("GET", url)
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    try {
                        var json = JSON.parse(xhr.responseText)
                        if (json.current) {
                            temperature = Math.round(json.current.temperature_2m) + "°C"
                            condition = decodeWmoCode(json.current.weather_code)
                            var d = new Date()
                            updatedAt = Qt.formatTime(d, "hh:mm")
                        }
                    } catch (e) {
                        condition = "Parse Error"
                    }
                } else {
                    condition = "Network Error"
                }
            }
        }
        xhr.send()
    }

    function decodeWmoCode(code) {
        switch (code) {
            case 0: return "Clear sky"
            case 1: case 2: case 3: return "Partly cloudy"
            case 45: case 48: return "Foggy"
            case 51: case 53: case 55: return "Drizzle"
            case 61: case 63: case 65: return "Rain"
            case 71: case 73: case 75: return "Snow"
            case 95: case 96: case 99: return "Thunderstorm"
            default: return "Fair"
        }
    }

    fullRepresentation: Item {
        Layout.minimumWidth: Kirigami.Units.gridUnit * 12
        Layout.minimumHeight: Kirigami.Units.gridUnit * 6

        Rectangle {
            anchors.fill: parent
            color: "#000000"
            border.color: "#333333"
            border.width: 2
            radius: 4

            ColumnLayout {
                anchors.centerIn: parent
                spacing: 6

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: root.cityName.toUpperCase()
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 12
                    color: "#888888"
                }

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: root.temperature
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 42
                    font.bold: true
                    color: "#FFFFFF"
                }

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: root.condition
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 13
                    color: "#D71921" // Nothing red
                }

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: "Updated " + root.updatedAt
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 10
                    color: "#666666"
                }
            }
        }
    }

    compactRepresentation: Item {
        RowLayout {
            anchors.centerIn: parent
            spacing: 4
            Text {
                text: root.temperature
                font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                font.pixelSize: 16
                font.bold: true
                color: "#FFFFFF"
            }
        }
    }
}
