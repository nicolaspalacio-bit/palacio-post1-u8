#!/usr/bin/env bash
# Verifica la regla de dependencia de Clean Architecture sobre el codigo fuente:
# ningun circulo interno importa nada de un circulo externo.
#
#   domain   (Entities)     no importa: usecase, adapter, config, Spring, JPA, Validation
#   usecase  (Use Cases)    no importa: adapter, config, Spring, JPA, Validation
#
# Uso (desde la raiz del repositorio):  ./scripts/verificar-regla-de-dependencia.sh

set -u
RAIZ="src/main/java/com/example/auditoria"
FRAMEWORKS='org\.springframework|jakarta\.persistence|jakarta\.validation|org\.hibernate|com\.fasterxml'
fallos=0

revisar() {
  local capa="$1" prohibido="$2" etiqueta="$3"
  local hallazgos
  hallazgos=$(grep -rEn "^import (static )?(${prohibido})" "${RAIZ}/${capa}" || true)
  if [ -n "$hallazgos" ]; then
    printf '  FALLA  %-9s -> %s\n%s\n' "${capa}/" "${etiqueta}" "${hallazgos}"
    fallos=$((fallos + 1))
  else
    printf '  OK     %-9s no importa %s\n' "${capa}/" "${etiqueta}"
  fi
}

echo "Regla de dependencia: el codigo solo apunta hacia adentro"
echo
revisar domain  "com\.example\.auditoria\.(usecase|adapter|config)" "circulos externos (usecase, adapter, config)"
revisar domain  "${FRAMEWORKS}"                                     "frameworks (Spring, JPA, Validation, Hibernate, Jackson)"
revisar usecase "com\.example\.auditoria\.(adapter|config)"         "circulos externos (adapter, config)"
revisar usecase "${FRAMEWORKS}"                                     "frameworks (Spring, JPA, Validation, Hibernate, Jackson)"
echo

if [ "$fallos" -eq 0 ]; then
  echo "Resultado: la regla de dependencia se cumple."
else
  echo "Resultado: ${fallos} violacion(es) de la regla de dependencia."
  exit 1
fi
