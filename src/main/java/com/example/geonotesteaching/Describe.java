package com.example.geonotesteaching;

import com.example.geonotesteaching.Attachment.*;

// Esta clase emplea 'switch expressions' y 'pattern matching' para representar un 'Attachment'.
// Los 'switch expressions' permiten que el 'switch' se utilice como una expresión que produce un resultado.
// El 'pattern matching' en cada 'case' posibilita extraer datos del objeto y
// añadir una condición ('when') de manera más compacta.
// Java requiere que el switch sea completo cuando se usa como expresión, por eso
// todos los tipos concretos de Attachment deben estar contemplados en un case.
// El primer case de Video se ejecuta únicamente si el video tiene una duración mayor a 120 segundos.
// El segundo case actúa como un "respaldo" que cubre los demás casos del tipo Video que no cumplen la condición.
// Se ha cambiado el identificador de la variable 'a' a 'audio' porque el compilador no distingue
// si 'a' correspondía al argumento del método o al patrón del case.


final class Describe {
    public static String describeAttachment(Attachment a) {
        return switch (a) {
            case Photo p when p.width() > 1920 -> "📷 Foto en alta definición (%d x %d)".formatted(p.width(), p.height());
            case Photo p -> "📷 Foto";
            case Audio audio when audio.duration() > 300 -> {
                var mins = audio.duration() / 60;
                yield " Audio (" + mins + " min)" ;
            }
            case Audio audio -> "🎵 Audio";
            case Link l -> "🔗 %s".formatted((l.label() == null || l.label().isEmpty()) ? l.url() : l.label());
            case Video v when v.seconds() > 120-> "￿ Vídeo largo";
            case Video v-> "￿ Vídeo";
        };
    }

        static int mediaPixels(Object o) {
            if(o instanceof Photo) {
                return ((Photo) o).width() * ((Photo) o).height();
            } else if (o instanceof Video) {
                return ((Video) o).width() * ((Video) o).height();
            } else {
                return 0;
            }
        }


}