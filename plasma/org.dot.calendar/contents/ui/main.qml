import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    preferredWidth: units.gridUnit * 14
    preferredHeight: units.gridUnit * 10

    property var currentDate: new Date()

    FontLoader {
        id: dotoFont
        source: "../../../design/fonts/Doto.ttf"
    }

    fullRepresentation: Item {
        Layout.minimumWidth: units.gridUnit * 12
        Layout.minimumHeight: units.gridUnit * 8

        Rectangle {
            anchors.fill: parent
            color: "#000000"
            border.color: "#333333"
            border.width: 2
            radius: 4

            ColumnLayout {
                anchors.fill: parent
                anchors.margins: 10
                spacing: 8

                // Header: Month & Year
                RowLayout {
                    Layout.fillWidth: true
                    Text {
                        text: Qt.formatDate(root.currentDate, "MMMM yyyy").toUpperCase()
                        font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                        font.pixelSize: 14
                        font.bold: true
                        color: "#FFFFFF"
                        Layout.fillWidth: true
                    }
                }

                // Days of week header
                RowLayout {
                    Layout.fillWidth: true
                    spacing: 2
                    property var days: ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"]
                    Repeater {
                        model: parent.days
                        Text {
                            Layout.fillWidth: true
                            horizontalAlignment: Text.AlignHCenter
                            text: modelData
                            font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                            font.pixelSize: 11
                            color: "#666666"
                        }
                    }
                }

                // Calendar grid (simplified current month view)
                GridLayout {
                    Layout.fillWidth: true
                    Layout.fillHeight: true
                    columns: 7
                    rows: 6
                    rowSpacing: 2
                    columnSpacing: 2

                    // Generate days for current month
                    property int firstDayIndex: {
                        var d = new Date(root.currentDate.getFullYear(), root.currentDate.getMonth(), 1)
                        var day = d.getDay()
                        return day === 0 ? 6 : day - 1 // Monday start
                    }
                    property int daysInMonth: new Date(root.currentDate.getFullYear(), root.currentDate.getMonth() + 1, 0).getDate()
                    property int today: root.currentDate.getDate()

                    Repeater {
                        model: 42 // 6 rows * 7 cols
                        Item {
                            Layout.fillWidth: true
                            Layout.fillHeight: true

                            property int dayNum: index - parent.firstDayIndex + 1
                            property bool isCurrentMonth: dayNum > 0 && dayNum <= parent.daysInMonth
                            property bool isToday: isCurrentMonth && dayNum === parent.today

                            Rectangle {
                                anchors.fill: parent
                                color: isToday ? "#D71921" : "transparent"
                                radius: 2

                                Text {
                                    anchors.centerIn: parent
                                    text: isCurrentMonth ? dayNum : ""
                                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                                    font.pixelSize: 12
                                    font.bold: isToday
                                    color: isToday ? "#FFFFFF" : (isCurrentMonth ? "#CCCCCC" : "#333333")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    compactRepresentation: Item {
        Text {
            anchors.centerIn: parent
            text: Qt.formatDate(root.currentDate, "d MMM")
            font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
            font.pixelSize: 14
            font.bold: true
            color: "#FFFFFF"
        }
    }
}
