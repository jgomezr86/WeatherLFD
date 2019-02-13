# Componente "Tiempo"

Este componente proporciona una manera de comprobar y notificar la temperatura actual, en tiempo real de Sabadell. Tiene varios comandos de configuración (ref. _[Eventos](#tipos-de-comandos)_) y algunas dependencias vitales (ref. _[Dependencias](#dependencias)_).


## Estructura general

### Comandos
La estructura general de comandos JSONObject, utilizados para la comunicación con el componente.


Comando:
```javascript
{
	"command": "weather.{tipo}"
}
```
(Otros campos según el comando)


Respuesta correcta:
```javascript
{
	"success": true,
	"data": "?" // Mensaje de respuesta
}
```

Respuesta incorrecta:
```javascript
{
	"success": false,
	"data": "?" // Mensaje de error
}
```

## Tipos de comandos

Diferentes tipos de commandos que controlan el componente. (ref. apartado _[Estructura general](#estructura-general)_)

### Activar

Comando que activa la notificación programada de cambios de temperatura. Utiliza el tiempo especificado por defecto o con comando (ref. apartado _[Tiempo](#tiempo)_)

Ejemplo:
```json
{
	"command": "weather.enable"
}
```

### Desactivar

Evento que desactiva la notificación programada. Utiliza el tiempo especificado por defecto o con comando (ref. apartado _[Tiempo](#tiempo)_) para finalizar el componente. 

Ejemplo:
```json
{
	"command": "weather.disable"
}
```

### Tiempo

Evento utilizado para notificar el tiempo de refresco de la comprobación de la temperatura  (ref. apartado _[Dependencias](#dependencias)_).

Ejemplo:
```javascript
{
	"command": "weather.settime",
	"time": 1 // Tiempo de actualización
}
```
## Tipos de Eventos

Existen dos clases de eventos controlados unos recibidos por IEventListener commandos (ref. apartado _[Estructura general](#estructura-general)_) y solicitud de temperatura e otro generado por una fuente externa, darksky.net (ref. apartado _[Dependencias](#dependencias)_).

### IEventListener commandos 

Todos los comandos explicados en los apartados Estructura general y Tipos de Commandos.

### IEventListener Hashtag #laferreriadam

Evento recibido por IEventListener que envía un Tweet, con la temperatura y el estado del cielo de Sabadell, al usuario emisor del Tweet a  #laferreriadam con el hashtag #quetemperaturaLFD (ref. apartado _[Tiempo](#tiempo)_)

Ejemplo:
```json
{
	"status": "hashtags"
}
```

### darksky.net canvio de temperatura

Cuando tras realizar una comprobación de la temperatura por la API de darksky.net (ref. apartado _[Dependencias](#dependencias)_), según el tiempo establecido por defecto 15 minutos o la especificada por el comando "weather.settime" , de haber un cambio se envía un Tweet a #laferreriadam.

## Dependencias

En este apartado citaremos los componentes necesarios para el funcionamiento del componente _Weather_.

*   Página web _[DarkSky](https://darksky.net)_. Se usa como fuente de información meteorológica.
*   Librería _[org.json](https://github.com/stleary/JSON-java)_ versión mínima del 2018.08.13
*   Librería Commander versión mínima 1.7 (componente _Storage_)
