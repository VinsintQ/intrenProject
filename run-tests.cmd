@echo off
echo ========================================
echo Running CryptoPeak Test Suite
echo ========================================
echo.

call mvnw clean test

echo.
echo ========================================
echo Test execution completed
echo ========================================
pause
