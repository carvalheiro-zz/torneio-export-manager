# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre-alpine
# Instala fontes para o Apache POI (AutoSize) funcionar no Linux
RUN apk add --no-cache fontconfig ttf-dejavu

COPY --from=build /target/*.jar app.jar

# Expõe a porta que o Render vai usar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]