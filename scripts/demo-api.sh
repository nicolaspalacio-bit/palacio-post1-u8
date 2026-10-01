#!/usr/bin/env bash
# Recorre el ciclo de vida completo de un hallazgo contra la API en ejecucion -incluyendo
# el dashboard y el historial de la Parte 2- y muestra el codigo HTTP de cada llamada.
# Sirve para generar las capturas de evidencia del README.
#
# Requisitos: la aplicacion levantada (mvn spring-boot:run) y curl.
# Uso:        ./scripts/demo-api.sh            (usa http://localhost:8080)
#             BASE_URL=http://host:puerto/api/hallazgos ./scripts/demo-api.sh

set -u
BASE="${BASE_URL:-http://localhost:8080/api/hallazgos}"
ACTOR="${ACTOR:-nicolas.palacio}"

llamar() {
  local metodo="$1" url="$2" cuerpo="${3:-}"
  local args=(-s -w '\nHTTP %{http_code}\n' -X "$metodo" "$url" -H "X-Actor: $ACTOR")
  if [ -n "$cuerpo" ]; then args+=(-H 'Content-Type: application/json' -d "$cuerpo"); fi
  curl "${args[@]}"
}

paso() { printf '\n== %s ==\n' "$1"; }

paso "1. Registrar un hallazgo (espera 201 Created)"
respuesta=$(llamar POST "$BASE" '{"titulo":"Contrasenas por defecto en servidor de pruebas","descripcion":"El servidor QA usa credenciales por defecto del fabricante","areaResponsable":"Infraestructura","severidad":"ALTA","fechaDeteccion":"2026-08-01"}')
echo "$respuesta"
ID=$(echo "$respuesta" | sed -n 's/.*"hallazgoId":"\([^"]*\)".*/\1/p')

paso "2. Registrar otro hallazgo que se dejara ABIERTO"
respuesta=$(llamar POST "$BASE" '{"titulo":"Accesos sin revisar en el ERP","descripcion":"Cuentas de ex empleados activas","areaResponsable":"Seguridad TI","severidad":"CRITICA","fechaDeteccion":"2026-08-03"}')
echo "$respuesta"
OTRO=$(echo "$respuesta" | sed -n 's/.*"hallazgoId":"\([^"]*\)".*/\1/p')

paso "3. Cerrar sin haber iniciado remediacion (espera 400)"
llamar PATCH "$BASE/$OTRO/cerrar"

paso "4. Iniciar remediacion del primer hallazgo (espera 200, EN_REMEDIACION)"
llamar PATCH "$BASE/$ID/iniciar-remediacion" '{"responsable":"Equipo de Infraestructura","fechaLimite":"2026-08-20","notas":"Rotar credenciales"}'

paso "5. Reabrir un hallazgo que sigue en remediacion (espera 400)"
llamar PATCH "$BASE/$ID/reabrir" '{"motivo":"Prueba de transicion invalida"}'

paso "6. Cerrar el primer hallazgo (espera 200, CERRADO)"
llamar PATCH "$BASE/$ID/cerrar"

paso "7. Reabrirlo con un motivo (espera 200, REABIERTO)"
llamar PATCH "$BASE/$ID/reabrir" '{"motivo":"Las credenciales por defecto reaparecieron tras una migracion"}'

paso "8. Consultar el hallazgo (espera 200)"
llamar GET "$BASE/$ID"

paso "9. Listar todos los hallazgos (espera 200)"
llamar GET "$BASE"

paso "10. Consultar el historial cronologico del hallazgo (espera 200, 3 eventos en orden)"
llamar GET "$BASE/$ID/historial"

paso "11. Consultar el dashboard consolidado (espera 200)"
llamar GET "$BASE/dashboard"
