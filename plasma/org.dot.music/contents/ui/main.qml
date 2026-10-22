import org.kde.kirigami as Kirigami
import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    preferredWidth: Kirigami.Units.gridUnit * 14
    preferredHeight: Kirigami.Units.gridUnit * 6

    property string trackTitle: "No Media Playing"
    property string artistName: "Nothing OS"
    property bool isPlaying: false

    FontLoader {
        id: dotoFont
        source: "../fonts/Doto.ttf"
    }

    fullRepresentation: Item {
        Layout.minimumWidth: Kirigami.Units.gridUnit * 12
        Layout.minimumHeight: Kirigami.Units.gridUnit * 5

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
                    text: root.trackTitle.toUpperCase()
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    font.bold: true
                    color: "#FFFFFF"
                }

                Text {
                    Layout.alignment: Qt.AlignHCenter
                    text: root.artistName
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 12
                    color: "#D71921"
                }

                RowLayout {
                    Layout.alignment: Qt.AlignHCenter
                    spacing: 16
                    Text {
                        text: "⏮"
                        font.pixelSize: 16
                        color: "#888888"
                    }
                    Text {
                        text: root.isPlaying ? "⏸" : "▶"
                        font.pixelSize: 18
                        color: "#FFFFFF"
                    }
                    Text {
                        text: "⏭"
                        font.pixelSize: 16
                        color: "#888888"
                    }
                }
            }
        }
    }

    compactRepresentation: Item {
        Text {
            anchors.centerIn: parent
            text: "🎵"
            font.pixelSize: 16
        }
    }
}
