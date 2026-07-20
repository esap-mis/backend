# 1. Запустить кластер
minikube start --cpus=4 --memory=5926

# 2. Собрать Docker-образ внутри кластера
# eval $(minikube docker-env)
# docker build -t esap:local .
minikube image load esap-mis-core
minikube image load esap-mis-notification-service
minikube image load esapmis/frontend
# minikube addons enable ingress

# 3. Применить манифесты
kubectl apply -f k8s
kubectl apply -f k8s/kafka
kubectl apply -f k8s/postgres
kubectl apply -f k8s/notification-service
kubectl apply -f k8s/esap-core
kubectl apply -f k8s/frontend

# 4. Проверить статус
kubectl get pods -w
kubectl logs deployment/esap-app

# 5. Открыть доступ к приложению
# kubectl port-forward service/esap-service 8080:8080
kubectl port-forward service/frontend-service 8081:80 # Открыть в браузере: http://localhost:8081

# kubectl delete all --all -n default

# 6. (Опционально) Включить Ingress
#minikube addons enable ingress