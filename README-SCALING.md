# Application Scaling Configuration

This document describes the scaling configuration for the Product Service application, optimized for cost-effective deployment.

## 🎯 Scaling Strategy

### Current Configuration
- **Minimum Replicas**: 0
- **Maximum Replicas**: 1
- **CPU**: 0.25 cores
- **Memory**: 0.5 GiB

### Why This Configuration?

This scaling setup is perfect for:
- **Development environments**
- **Testing scenarios**
- **Low-traffic production applications**
- **Cost-sensitive deployments**
- **Proof of concept (POC) projects**

## 💰 Cost Benefits

### Estimated Monthly Costs
- **Ultra-scaled configuration**: $0.05 - $0.15/month
- **Standard configuration**: $0.20 - $0.50/month
- **Cost savings**: ~70-80% reduction

### How It Works
1. **Scale to Zero**: When no requests for 5+ minutes, the app scales to 0 replicas
2. **Cold Start**: First request triggers scale-up (10-30 second delay)
3. **Single Instance**: Maximum 1 instance prevents over-provisioning
4. **Auto-scaling**: Automatically scales based on demand

## 🚀 Deployment Options

### Option 1: Scaled Production Deployment
```bash
# Deploy with scaled configuration
./deploy.sh scaled

# Or use the specific script
./deploy-azure-scaled.sh
```

### Option 2: Standard Production Deployment
```bash
# Deploy with standard configuration
./deploy.sh prod

# Or use the specific script
./deploy-azure.sh
```

## ⚙️ Configuration Details

### Azure Container Apps Settings
```yaml
min-replicas: 0
max-replicas: 1
cpu: 0.25
memory: 0.5Gi
```

### Application Settings (Updated)
```properties
# Scaled down for cost optimization
server.tomcat.threads.max=50
server.tomcat.threads.min-spare=5
server.tomcat.max-connections=1000
server.tomcat.accept-count=50
```

## 📊 Performance Characteristics

### Cold Start Behavior
- **First request**: 10-30 seconds (cold start)
- **Subsequent requests**: Normal response time
- **Scale-down time**: ~5 minutes of inactivity

### Resource Usage
- **CPU**: 0.25 cores (25% of 1 core)
- **Memory**: 0.5 GiB (512 MB)
- **Concurrent connections**: Up to 1000
- **Thread pool**: 50 max threads

## 🔄 Scaling Behavior

### Scale Up Triggers
- First incoming request
- Health check requests
- Any API endpoint call

### Scale Down Triggers
- No requests for 5+ minutes
- No active connections
- Idle state maintained

### Monitoring
- Azure Container Apps metrics
- Application health checks
- Custom monitoring via Spring Actuator

## 🛠️ Customization Options

### Adjust Scaling Parameters
To modify scaling behavior, update the deployment script:

```bash
# In deploy-azure-scaled.sh
--min-replicas 0 \
--max-replicas 1 \
--cpu 0.25 \
--memory 0.5Gi \
```

### Adjust Application Settings
To modify application performance, update `application-azure.properties`:

```properties
# Increase for higher throughput
server.tomcat.threads.max=100
server.tomcat.max-connections=2000

# Decrease for lower resource usage
server.tomcat.threads.max=25
server.tomcat.max-connections=500
```

## 📈 When to Scale Up

Consider increasing scaling limits when:
- **Traffic increases** beyond single instance capacity
- **Response times** become unacceptable
- **Concurrent users** exceed 100-200
- **Business requirements** demand higher availability

### Recommended Scaling Tiers

#### Tier 1: Ultra-Scaled (Current)
- Min: 0, Max: 1
- Cost: $0.05-0.15/month
- Use case: Development, POC, very low traffic

#### Tier 2: Light Production
- Min: 1, Max: 3
- Cost: $0.20-0.60/month
- Use case: Small production, moderate traffic

#### Tier 3: Standard Production
- Min: 2, Max: 10
- Cost: $0.50-2.00/month
- Use case: Production, high availability

## 🔍 Monitoring and Alerts

### Key Metrics to Monitor
- **Replica count**: Current active instances
- **CPU usage**: Should stay under 80%
- **Memory usage**: Should stay under 90%
- **Response time**: Cold start vs warm requests
- **Request rate**: Requests per minute

### Recommended Alerts
- High CPU usage (>80%)
- High memory usage (>90%)
- Frequent cold starts
- Response time degradation

## 🚨 Troubleshooting

### Common Issues

#### Cold Start Delays
- **Symptom**: First request takes 10-30 seconds
- **Solution**: Normal behavior for scale-to-zero
- **Mitigation**: Keep-alive requests or increase min replicas

#### Resource Constraints
- **Symptom**: High CPU/memory usage
- **Solution**: Increase CPU/memory allocation
- **Check**: Application performance and resource usage

#### Scaling Failures
- **Symptom**: App doesn't scale up/down
- **Solution**: Check Azure Container Apps logs
- **Verify**: Resource quotas and limits

## 📚 Additional Resources

- [Azure Container Apps Scaling](https://docs.microsoft.com/en-us/azure/container-apps/scale-app)
- [Cost Optimization Guide](https://docs.microsoft.com/en-us/azure/container-apps/cost-optimization)
- [Performance Tuning](https://docs.microsoft.com/en-us/azure/container-apps/performance-tuning)






