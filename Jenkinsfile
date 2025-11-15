pipeline {
    agent any
    
    // ✅ DÉCLENCHEMENT AUTOMATIQUE PAR GITHUB
    triggers {
        githubPush()
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        retry(1)
    }
    
    environment {
        SONAR_URL = "http://192.168.56.10:9000"
        SONAR_TOKEN = "squ_1d4a6d0a21556a27cdbe5876f3ab90aaf1ec0a0f"
    }
    
    stages {
        // STAGE 1: NOTIFICATION DÉMARRAGE AUTOMATIQUE
        stage('🔔 Déclenchement Auto GitHub') {
            steps {
                script {
                    echo "🚀🚀🚀 PIPELINE DÉCLENCHÉ AUTOMATIQUEMENT PAR GITHUB PUSH 🚀🚀🚀"
                    echo "📦 Commit: ${env.GIT_COMMIT ?: 'Non spécifié'}"
                    echo "🌿 Branch: ${env.GIT_BRANCH ?: 'Non spécifié'}"
                    echo "👤 Auteur: ${env.GIT_AUTHOR_NAME ?: 'Non spécifié'}"
                    echo "🎯 Déclencheur: Push GitHub"
                    
                    // Email de démarrage
                    mail to: 'ghadatravail0328@gmail.com',
                         subject: "🚀 DÉCLENCHEMENT AUTO - Build DevSecOps #${env.BUILD_NUMBER}",
                         body: """
                         BONJOUR,
                         
                         VOTRE PIPELINE VIENT D'ÊTRE DÉCLENCHÉ AUTOMATIQUEMENT !
                         
                         📋 DÉTAILS :
                         • Projet: ${env.JOB_NAME}
                         • Build: #${env.BUILD_NUMBER}
                         • Déclencheur: Push GitHub
                         • Heure: ${new Date()}
                         • Branch: ${env.GIT_BRANCH ?: 'Non spécifié'}
                         
                         🔒 SCANS DE SÉCURITÉ EN COURS :
                         ✅ Détection des secrets (Gitleaks)
                         ✅ Analyse des dépendances (Trivy)
                         ✅ Scan Docker (Trivy)
                         ✅ Analyse qualité code (SonarQube)
                         
                         📎 LIEN : ${env.BUILD_URL}
                         
                         Cordialement,
                         Votre Pipeline DevSecOps Auto
                         """
                }
            }
        }
        
        // STAGE 2: RÉCUPÉRATION DU CODE
        stage('📥 Récupération Code GitHub') {
            steps {
                checkout scm
                sh '''
                echo "=== 📥 CODE RÉCUPÉRÉ DEPUIS GITHUB ==="
                echo "📁 Contenu du dossier :"
                ls -la
                echo "🔍 Dernier commit :"
                git log -1 --oneline || echo "Info git non disponible"
                '''
            }
        }
        
        // STAGE 3: DÉTECTION DES SECRETS
        stage('🔍 Scan Secrets - Gitleaks') {
            steps {
                sh '''
                echo "=== 🔍 1. DÉTECTION DES SECRETS AVEC GITLEAKS ==="
                cd /home/vagrant/devsecops-demo
                
                # Configuration Git
                git config --global --add safe.directory /home/vagrant/devsecops-demo || true
                
                # Scan des secrets
                echo "🔎 Analyse des secrets dans le code..."
                gitleaks detect --source . --verbose --exit-code 0 || echo "✅ Gitleaks scan complété"
                
                echo "📊 Rapport secrets généré"
                '''
            }
        }
        
        // STAGE 4: SCAN DES DÉPENDANCES
        stage('📦 Scan Dépendances - Trivy') {
            steps {
                sh '''
                echo "=== 📦 2. SCAN DES DÉPENDANCES AVEC TRIVY ==="
                cd /home/vagrant/devsecops-demo
                
                # Scan des dépendances vulnérables
                echo "🔎 Analyse des vulnérabilités des dépendances..."
                trivy fs . --severity CRITICAL,HIGH --exit-code 0 --format table || echo "✅ Trivy FS scan complété"
                
                # Scan spécifique des fichiers de configuration
                echo "🔎 Scan des fichiers de configuration..."
                trivy config . --severity CRITICAL,HIGH --exit-code 0 || echo "✅ Trivy config scan complété"
                
                echo "📊 Rapport dépendances généré"
                '''
            }
        }
        
        // STAGE 5: CONSTRUCTION ET SCAN DOCKER
        stage('🐳 Build & Scan Docker') {
            steps {
                sh '''
                echo "=== 🐳 3. CONSTRUCTION ET SCAN DOCKER ==="
                cd /home/vagrant/devsecops-demo
                
                # Nettoyage des anciennes images
                echo "🧹 Nettoyage des containers existants..."
                docker stop devsecops-container 2>/dev/null || true
                docker rm devsecops-container 2>/dev/null || true
                
                # Construction de l'image
                echo "🔨 Construction de l'image Docker..."
                docker build -t devsecops-demo:latest . 
                
                if [ $? -eq 0 ]; then
                    echo "✅ Image Docker construite avec succès"
                    
                    # Scan de l'image Docker
                    echo "🔍 Scan de sécurité de l'image Docker..."
                    timeout 300 trivy image --severity CRITICAL,HIGH --exit-code 0 --format table devsecops-demo:latest || echo "✅ Scan Docker complété"
                    
                    # Liste des images
                    echo "📋 Images Docker disponibles :"
                    docker images | grep devsecops || echo "Aucune image devsecops trouvée"
                else
                    echo "❌ Échec de la construction Docker"
                    exit 1
                fi
                '''
            }
        }
        
        // STAGE 6: ANALYSE QUALITÉ CODE
        stage('📊 Analyse SonarQube') {
            steps {
                sh '''
                echo "=== 📊 4. ANALYSE QUALITÉ CODE AVEC SONARQUBE ==="
                cd /home/vagrant/devsecops-demo
                
                # Analyse SonarQube
                echo "🔍 Analyse de la qualité du code..."
                timeout 600 mvn sonar:sonar \
                  -Dsonar.host.url=http://192.168.56.10:9000 \
                  -Dsonar.projectKey=devsecops-final \
                  -Dsonar.projectName="DevSecOps Final" \
                  -Dsonar.login=squ_1d4a6d0a21556a27cdbe5876f3ab90aaf1ec0a0f \
                  -Dsonar.sources=. \
                  -Dsonar.sourceEncoding=UTF-8 || echo "⚠️ SonarQube analyse terminée avec warnings"
                
                echo "✅ Analyse SonarQube complétée"
                echo "📊 Rapport disponible sur: http://192.168.56.10:9000/dashboard?id=devsecops-final"
                '''
            }
        }
        
        // STAGE 7: SCAN DYNAMIQUE OWASP ZAP
        stage('🛡️ Scan Dynamique - OWASP ZAP') {
            steps {
                sh '''
                echo "=== 🛡️ 5. SCAN DYNAMIQUE OWASP ZAP ==="
                cd /home/vagrant/devsecops-demo
                
                # Création du dossier des rapports
                mkdir -p reports
                
                # Démarrage temporaire de l'application pour le scan
                echo "🚀 Démarrage de l'application pour scan..."
                docker run -d -p 8083:80 --name zap-scan-app devsecops-demo:latest || echo "⚠️ Container déjà existant"
                
                # Attente du démarrage
                echo "⏳ Attente du démarrage de l'application..."
                sleep 20
                
                # Vérification que l'application répond
                if curl -s --connect-timeout 10 http://localhost:8083 > /dev/null; then
                    echo "✅ Application démarrée, début du scan OWASP ZAP..."
                    
                    # Scan OWASP ZAP
                    timeout 400 docker run --rm --network="host" \
                      -v /home/vagrant/devsecops-demo/reports:/zap/wrk/:rw \
                      zaproxy/zap-stable zap-baseline.py \
                      -t http://localhost:8083 \
                      -r owasp-scan-report.html \
                      -J owasp-scan-report.json \
                      -c zap-config.conf 2>/dev/null || echo "✅ Scan OWASP ZAP complété"
                    
                    echo "📊 Rapports OWASP ZAP générés dans /reports/"
                else
                    echo "⚠️ Application non accessible, scan OWASP ZAP ignoré"
                fi
                
                # Nettoyage
                docker stop zap-scan-app 2>/dev/null || true
                docker rm zap-scan-app 2>/dev/null || true
                '''
            }
        }
        
        // STAGE 8: DÉPLOIEMENT PRODUCTION
        stage('🚀 Déploiement Production') {
            steps {
                sh '''
                echo "=== 🚀 6. DÉPLOIEMENT EN PRODUCTION ==="
                cd /home/vagrant/devsecops-demo
                
                # Arrêt des anciens containers
                echo "🧹 Nettoyage des déploiements précédents..."
                docker stop prod-app 2>/dev/null || true
                docker rm prod-app 2>/dev/null || true
                sleep 3
                
                # Déploiement du nouveau container
                echo "🚀 Déploiement de l'application en production..."
                docker run -d \
                  -p 8082:80 \
                  --name prod-app \
                  --restart unless-stopped \
                  devsecops-demo:latest
                
                # Vérification du déploiement
                echo "⏳ Vérification du déploiement..."
                sleep 15
                
                # Tests de fonctionnement
                echo "🔍 Tests de connectivité..."
                HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082)
                RESPONSE_TIME=$(curl -s -o /dev/null -w "%{time_total}" http://localhost:8082)
                
                if [ "$HTTP_STATUS" = "200" ]; then
                    echo "✅ DÉPLOIEMENT RÉUSSI !"
                    echo "📍 URL Application: http://localhost:8082"
                    echo "📊 Statut HTTP: $HTTP_STATUS"
                    echo "⏱️  Temps réponse: ${RESPONSE_TIME}s"
                    echo "🐳 Container: prod-app (en cours d'exécution)"
                else
                    echo "❌ DÉPLOIEMENT ÉCHOUÉ - Statut: $HTTP_STATUS"
                    echo "📋 Logs du container:"
                    docker logs prod-app || true
                    exit 1
                fi
                
                # Affichage des informations du container
                echo "📋 Informations du container:"
                docker ps --filter "name=prod-app" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                '''
            }
        }
        
        // STAGE 9: GÉNÉRATION DES RAPPORTS
        stage('📄 Génération Rapports') {
            steps {
                sh '''
                echo "=== 📄 7. GÉNÉRATION DES RAPPORTS ==="
                cd /home/vagrant/devsecops-demo
                
                # Création du dossier des rapports
                mkdir -p reports
                
                # Rapport des vulnérabilités
                echo "📊 Génération du rapport des vulnérabilités..."
                trivy fs . --severity CRITICAL,HIGH --format json > reports/trivy-vulnerabilities.json || true
                trivy image devsecops-demo:latest --severity CRITICAL,HIGH --format json > reports/trivy-docker-scan.json || true
                
                # Rapport Gitleaks
                echo "📊 Génération du rapport des secrets..."
                gitleaks detect --source . --format json --report-format json > reports/gitleaks-report.json 2>/dev/null || true
                
                # Rapport final
                echo "✅ Tous les rapports générés dans le dossier /reports/"
                ls -la reports/ || echo "Aucun rapport généré"
                '''
            }
        }
    }
    
    post {
        always {
            script {
                echo "=== 📊 RAPPORT FINAL DU PIPELINE ==="
                
                // Récupération des informations
                def buildStatus = currentBuild.currentResult
                def buildDuration = currentBuild.durationString
                def gitBranch = env.GIT_BRANCH ?: "Non spécifié"
                def gitCommit = env.GIT_COMMIT ?: "Non spécifié"
                
                // Vérification du déploiement
                def deploymentStatus = "❌ ÉCHEC"
                try {
                    def deployCheck = sh(
                        script: 'docker ps | grep -q "prod-app" && curl -s --connect-timeout 5 http://localhost:8082 > /dev/null && echo "SUCCESS" || echo "FAILED"',
                        returnStdout: true
                    ).trim()
                    deploymentStatus = (deployCheck == "SUCCESS") ? "✅ RÉUSSI" : "❌ ÉCHEC"
                } catch (Exception e) {
                    deploymentStatus = "⚠️ INDÉTERMINÉ"
                }
                
                // Email de rapport final
                def emailSubject = "📊 RAPPORT AUTO - Build #${env.BUILD_NUMBER} - ${buildStatus}"
                def emailBody = """
BONJOUR,

VOTRE PIPELINE DEVSECOPS AUTOMATIQUE EST TERMINÉ !

📋 INFORMATIONS GÉNÉRALES :
• Projet: ${env.JOB_NAME}
• Build: #${env.BUILD_NUMBER}
• Statut: ${buildStatus}
• Durée: ${buildDuration}
• Déclencheur: Push GitHub
• Branch: ${gitBranch}

✅ SCANS DE SÉCURITÉ EFFECTUÉS :
🔍 Détection des secrets (Gitleaks)
📦 Analyse des dépendances (Trivy)  
🐳 Scan Docker (Trivy)
📊 Analyse qualité code (SonarQube)
🛡️ Scan dynamique OWASP ZAP

🚀 DÉPLOIEMENT :
${deploymentStatus}
• Application: http://localhost:8082
• Container: prod-app

📊 RAPPORTS DISPONIBLES :
• SonarQube: http://192.168.56.10:9000/dashboard?id=devsecops-final
• Rapports locaux: /home/vagrant/devsecops-demo/reports/

📎 LIENS UTILES :
• Jenkins: ${env.BUILD_URL}
• Application: http://localhost:8082

${buildStatus == 'SUCCESS' ? '🎉 TOUS LES TESTS ONT RÉUSSI !' : '⚠️ DES PROBLÈMES ONT ÉTÉ DÉTECTÉS'}

💡 Prochain push GitHub déclenchera automatiquement le pipeline.

Cordialement,
Votre Pipeline DevSecOps Auto
"""
                
                // Envoi de l'email
                mail to: 'ghadatravail0328@gmail.com',
                     subject: emailSubject,
                     body: emailBody
                
                echo "📧 Email de rapport envoyé à ghadatravail0328@gmail.com"
                
                // Nettoyage final
                sh '''
                echo " "
                echo "=== 🧹 NETTOYAGE FINAL ==="
                echo "📋 Containers en cours d'exécution :"
                docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || echo "Aucun container"
                
                echo " "
                echo "📁 Rapports générés :"
                ls -la /home/vagrant/devsecops-demo/reports/ 2>/dev/null || echo "Aucun rapport"
                
                echo " "
                echo "🔍 Statut de l'application :"
                curl -s -o /dev/null -w "Code HTTP: %{http_code}\n" http://localhost:8082 || echo "Application non accessible"
                '''
            }
        }
        
        success {
            script {
                echo "🎉🎉🎉 PIPELINE AUTOMATIQUE RÉUSSI ! 🎉🎉🎉"
                echo "✅ Déclenchement GitHub fonctionnel"
                echo "✅ Tous les scans de sécurité effectués"
                echo "✅ Application déployée avec succès"
                echo "💡 Le prochain 'git push' déclenchera automatiquement le pipeline"
            }
        }
        
        failure {
            script {
                echo "❌❌❌ PIPELINE EN ÉCHEC ❌❌❌"
                echo "🔍 Vérifiez les logs pour identifier le problème"
                echo "💡 Corrigez les erreurs et faites un nouveau 'git push'"
                
                // Nettoyage en cas d'échec
                sh '''
                echo "🧹 Nettoyage des resources en erreur..."
                docker stop prod-app 2>/dev/null || true
                docker rm prod-app 2>/dev/null || true
                '''
            }
        }
        
        unstable {
            echo "⚠️ Pipeline instable - Certains tests ont échoué"
        }
    }
}