import org.kde.kirigami as Kirigami
import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    Layout.preferredWidth: Kirigami.Units.gridUnit * 12
    Layout.preferredHeight: Kirigami.Units.gridUnit * 6

    FontLoader {
        id: dotoFont
        source: "../fonts/Doto.ttf"
    }

    Timer {
        interval: 1000
        running: true
        repeat: true
        triggeredOnStart: true
        onTriggered: {
            timeLabel.text = Qt.formatTime(new Date(), "hh:mm")
            dateLabel.text = Qt.formatDate(new Date(), "ddd, MMM d").toUpperCase()
        }
    }

    fullRepresentation: Item {
        Layout.minimumWidth: Kirigami.Units.gridUnit * 10
        Layout.minimumHeight: Kirigami.Units.gridUnit * 5

        Rectangle {
            anchors.fill: parent
            color: "#000000"
            border.color: "#333333"
            border.width: 2
            radius: 4

            ColumnLayout {
                anchors.centerIn: parent
                spacing: 4

                Text {
                    id: timeLabel
                    Layout.alignment: Qt.AlignHCenter
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 56
                    font.bold: true
                    color: "#FFFFFF"
                }

                Text {
                    id: dateLabel
                    Layout.alignment: Qt.AlignHCenter
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    color: "#D71921" // Nothing red accent
                }
            }
        }
    }

    compactRepresentation: Item {
        Text {
            anchors.centerIn: parent
            text: Qt.formatTime(new Date(), "hh:mm")
            font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
            font.pixelSize: 22
            font.bold: true
            color: "#FFFFFF"
        }
    }
}
