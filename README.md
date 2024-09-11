# Wikipedia Web Crawler

Este proyecto es un **web crawler** en Java diseñado para rastrear, descargar y almacenar el contenido de páginas de Wikipedia en español. Utiliza la librería Apache Tika para extraer y procesar el contenido de las páginas web.

## Características

- **Rastreo automático**: El crawler descarga el contenido HTML de las páginas de Wikipedia en español y sigue enlaces a otras páginas internas.
- **Descarga de contenido**: Guarda el contenido de cada página visitada en un archivo `.doc`.
- **Filtrado de enlaces**: Filtra enlaces irrelevantes (categorías, archivos, etc.) y evita visitar páginas duplicadas.
- **Control de visitas**: Puede rastrear hasta 200 páginas antes de detenerse (modificable).

## Requisitos

### Software

- Java 8 o superior
- Apache Maven (opcional, para gestionar dependencias)

