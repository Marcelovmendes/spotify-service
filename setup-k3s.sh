set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
echo_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
echo_error() { echo -e "${RED}[ERROR]${NC} $1"; }

echo_info "=== PlaySwap K3s Deploy Script ==="

echo ""
command -v k3s >/dev/null 2>&1 || { echo_error "K3s não encontrado!"; exit 1; }
command -v kubectl >/dev/null 2>&1 || { echo_error "kubectl não encontrado!"; exit 1; }
command -v docker >/dev/null 2>&1 || { echo_error "Docker não encontrado!"; exit 1; }
echo_info "✓ Ambiente OK"

if grep -q "YOUR_SPOTIFY_CLIENT_ID_HERE" k8s/secrets.yaml 2>/dev/null; then
    echo_warn "⚠️  Configure as secrets em k8s/secrets.yaml antes de continuar."
    read -p "Pressione ENTER após configurar..."
fi

echo ""
echo_info "=== 1/8 Build Docker image ==="
docker build -t spotify-service:latest .
echo_info "✓ Build concluído"

echo ""
echo_info "=== 2/8 Importando imagem para K3s ==="
docker save spotify-service:latest | sudo k3s ctr images import -
echo_info "✓ Imagem importada"

echo ""
echo_info "=== 3/8 Deploy Kubernetes Manifests ==="
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/postgres-deployments.yaml
kubectl apply -f k8s/redis-deployments.yaml
kubectl apply -f k8s/spotify-service-deployment.yaml
echo_info "✓ Manifests aplicados"

echo ""
echo_info "Aguardando PostgreSQL..."
kubectl wait --for=condition=ready pod -l app=postgres -n site --timeout=120s

echo ""
echo_info "Aguardando Redis..."
kubectl wait --for=condition=ready pod -l app=redis -n site --timeout=120s

echo ""
echo_info "Aguardando Spotify Service..."
kubectl wait --for=condition=ready pod -l app=spotify-service -n site --timeout=180s || {
    echo_warn "Timeout. Verificando pods:"
    kubectl get pods -n playswap
}

echo ""
echo_info "=== ✅ Deploy Concluído! ==="
kubectl get all -n playswap

echo ""
echo_info "Para acessar:"
echo_info "  kubectl port-forward -n playswap svc/spotify-service 8080:8080"
echo_info "Acesse: http://localhost:8080"
