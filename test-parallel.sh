#!/bin/bash
# Параллельный запуск тестов в Chrome и Firefox одновременно.
# Использование:
#   ./test-parallel.sh                                  # все тесты
#   ./test-parallel.sh '*MainPageTest*'                 # фильтр по имени класса/метода

TESTS=${1:-"*"}

echo "Running tests in Chrome and Firefox in parallel..."
echo "Filter: $TESTS"

./gradlew testChrome -Dtests="$TESTS" &
PID_CHROME=$!

./gradlew testFirefox -Dtests="$TESTS" &
PID_FIREFOX=$!

wait $PID_CHROME
EXIT_CHROME=$?

wait $PID_FIREFOX
EXIT_FIREFOX=$?

echo ""
if [ $EXIT_CHROME -eq 0 ]; then
  echo "Chrome: PASSED"
else
  echo "Chrome: FAILED (exit code $EXIT_CHROME)"
fi

if [ $EXIT_FIREFOX -eq 0 ]; then
  echo "Firefox: PASSED"
else
  echo "Firefox: FAILED (exit code $EXIT_FIREFOX)"
fi

[ $EXIT_CHROME -eq 0 ] && [ $EXIT_FIREFOX -eq 0 ]
