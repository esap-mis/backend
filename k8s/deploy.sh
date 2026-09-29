#!/usr/bin/env bash
set -e

# 1. Запустить кластер
minikube start --cpus=4 --memory=5926

# 2. Собрать образы всех сервисов плагином Spring Boot (Cloud Native Buildpacks)
# eval $(minikube docker-env)
./gradlew bootBuildImage
for m in api-gateway auth-service clinic-service schedule-service core notification-service; do
  minikube image load "esap-mis-$m"
done
minikube image load esapmis/frontend:v2

# 3. Применить манифесты (порядок важен: БД и Kafka поднимаются первыми)
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/kafka
kubectl apply -f k8s/esap-db
kubectl apply -f k8s/notifications-db
kubectl apply -f k8s/notification-service
kubectl apply -f k8s/auth-service
kubectl apply -f k8s/clinic-service
kubectl apply -f k8s/schedule-service
kubectl apply -f k8s/esap-core
kubectl apply -f k8s/api-gateway
kubectl apply -f k8s/frontend

# 4. Проверить статус
kubectl get pods -w
kubectl logs deployment/api-gateway
kubectl logs deployment/auth-service
kubectl logs deployment/clinic-service
kubectl logs deployment/schedule-service
kubectl logs deployment/esap-core
kubectl logs deployment/notification-service

# 5. Открыть доступ к приложению через gateway
# kubectl port-forward service/frontend-service 8081:80   # UI:  http://localhost:8081
kubectl port-forward service/api-gateway 8000:8000          # API: http://localhost:8000

# kubectl delete all --all -n default
