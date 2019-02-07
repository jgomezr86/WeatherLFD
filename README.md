# Componente "Tiempo"

Este componente proporciona una manera de comprobar y notificar la temperatura actual, en tiempo real de Sabadell. Tiene varios comandos de configuración (ref.
_[Eventos](#tipos-de-comandos)_) y algunas dependencias vitales (ref. _[Dependencias](#dependencias)_).


## Estructura general

La estructura general de comandos JSONObject, utilizada para la comunicación con el componente.


```
EVENTO:
{
	source (String):    weather
	type (String):      {tipo}
}

RESPUESTA CORRECTA:
{
	success (boolean):  true
	data (String):      mensaje de respuesta
}

RESPUESTA INCORRECTA:
{
	success (boolean):  false
	error (String):     mensaje de error
}
```

## Tipos de comandos

Diferentes tipos de eventos que emite el componente. (ref. apartado _[Estructura general](#estructura-general)_)

### Activar

Evento que activa la notificación programada de cambios de temperatura. Utiliza el tiempo especificado por defecto o con comando (ref. apartado _[Tiempo](#tiempo)_)

```
EJEMPLO:
{
	source (String): 	weather
	type (String): 		enable
}
```

### Desactivar

Evento que desactiva la notificación programada. Utiliza el tiempo especificado por defecto o con comando (ref. apartado _[Tiempo](#tiempo)_) para finalizar el componente. 

```
EJEMPLO:
{
	source (String): 	weather
	type (String): 		disable
}
```

### Tiempo

Evento utilizado para notificar el tiempo de refresco de la comprobación de la temperatura  (ref. apartado _[Dependencias](#dependencias)_).

```
EJEMPLO:
{
	source (String): 	weather
	type (String): 		timer
	time (Integer): 	tiempo de refresco
}
```

## Dependencias

En este apartado citaremos los componentes necesarios para el funcionamiento del componente _Weather_.

*   Página web _[DarkSky](https://darksky.net)_. Se usa como fuente de información meteorológica.
*   Librería _[org.json](https://github.com/stleary/JSON-java)_ versión mínima del 2018.08.13
*   Librería Commander versión mínima 1.7 (componente _Storage_)