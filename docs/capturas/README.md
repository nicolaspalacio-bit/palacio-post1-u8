# Capturas de evidencia

Carpeta para las capturas de pantalla de los endpoints en ejecución, referenciadas desde
la sección "Evidencia de ejecución" del README principal.

Con la aplicación levantada (`mvn spring-boot:run`), ejecutar `../../scripts/demo-api.sh`
y capturar la salida de cada paso (o el equivalente en Postman). Nombrar los archivos
según el paso que documentan, por ejemplo:

- `01-registrar-hallazgo.png`      -> POST /api/hallazgos (201)
- `03-cerrar-sin-remediacion.png`  -> PATCH .../cerrar sobre ABIERTO (400)
- `04-iniciar-remediacion.png`     -> PATCH .../iniciar-remediacion (200)
- `06-cerrar.png`                  -> PATCH .../cerrar (200)
- `07-reabrir.png`                 -> PATCH .../reabrir (200)
- `10-historial.png`               -> GET .../historial (200, 3 eventos en orden)
- `11-dashboard.png`               -> GET .../dashboard (200)
