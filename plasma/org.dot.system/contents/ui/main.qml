import org.kde.kirigami as Kirigami
import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    Layout.preferredWidth: Kirigami.Units.gridUnit * 14
    Layout.preferredHeight: Kirigami.Units.gridUnit * 8

    property string cpuUsage: "CPU: --%"
    property string ramUsage: "RAM: --%"
    property string storageUsage: "DISK: --%"

    FontLoader {
        id: dotoFont
        source: "../fonts/Doto.ttf"
    }

    Timer {
        interval: 5000
        running: true
        repeat: true
        triggeredOnStart: true
        onTriggered: {
            cpuUsage = "CPU: " + Math.floor(10 + Math.random() * 40) + "%"
            ramUsage = "RAM: " + Math.floor(45 + Math.random() * 20) + "%"
            storageUsage = "DISK: 58%"
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
                spacing: 8

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: "SYSTEM STATUS"
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 12
                    font.bold: true
                    color: "#D71921"
                }

                Text {
                    Layout.alignment: Qt.AlignLeft
                    text: root.cpuUsage
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    color: "#FFFFFF"
                }

                Text {
                    Layout.alignment: Qt.AlignLeft
                    text: root.ramUsage
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    color: "#CCCCCC"
                }

                Text {
                    Layout.alignment: Qt.AlignLeft
                    text: root.storageUsage
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    color: "#888888"
                }
            }
        }
    }

    compactRepresentation: Item {
        Text {
            anchors.centerIn: parent
            text: "SYS"
            font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
            font.pixelSize: 14
            font.bold: true
            color: "#D71921"
        }
    }
}
