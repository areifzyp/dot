import QtQuick
import QtQuick.Layouts
import org.kde.plasma.plasmoid
import org.kde.plasma.core as PlasmaCore

PlasmoidItem {
    id: root

    preferredWidth: units.gridUnit * 12
    preferredHeight: units.gridUnit * 8

    property string noteText: "Stay authentic. Nothing UI."

    FontLoader {
        id: dotoFont
        source: "../../../design/fonts/Doto.ttf"
    }

    fullRepresentation: Item {
        Layout.minimumWidth: units.gridUnit * 10
        Layout.minimumHeight: units.gridUnit * 6

        Rectangle {
            anchors.fill: parent
            color: "#000000"
            border.color: "#333333"
            border.width: 2
            radius: 4

            ColumnLayout {
                anchors.fill: parent
                anchors.margins: 12

                Text {
                    Layout.fillWidth: true
                    Layout.fillHeight: true
                    text: root.noteText
                    font.family: dotoFont.status === FontLoader.Ready ? dotoFont.name : "Monospace"
                    font.pixelSize: 14
                    color: "#FFFFFF"
                    wrapMode: Text.WordWrap
                }
            }
        }
    }

    compactRepresentation: Item {
        Text {
            anchors.centerIn: parent
            text: "📝"
            font.pixelSize: 16
        }
    }
}
