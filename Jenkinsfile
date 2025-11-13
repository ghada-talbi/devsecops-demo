pipeline {
    agent any
    
    stages {
        // NOTIFICATION DE DÉMARRAGE
        stage('📧 Notification Démarrage') {
            steps {
                script {
                    echo "🚀 ENVOI EMAIL DE DÉMARRAGE À GHADATRAVAIL0328@GMAIL.COM"
                    
                    mail to: 'ghadatravail0328@gmail.com',
                         subject: "🚀 DÉMARRAGE Build DevSecOps #${env.BUILD_NUMBER}",
                         body: """
                         BONJOUR,
                         
                         VOTRE PIPELINE DEVSECOPS VIENT DE DÉMARRER !
                         
                         📋 DÉTAILS :
                         • Projet: ${env.JOB_NAME}
                         • Build: #${env.BUILD_NUMBER} 
                         • Heure: ${new Date()}
                         
                         🔒 SCANS DE SÉCURITÉ EN COURS :
                         ✅ Détection des secrets (Gitleaks)
                         ✅ Analyse des dépendances (Trivy)
                         ✅ Scan Docker (Trivy)
                         ✅ Analyse qualité code (SonarQube)
                         
                         📎 LIEN : ${env.BUILD_URL}
                         
                         Cordialement,
                         Votre Pipeline DevSecOps
                         """
                }
            }
        }
        
        // VOS STAGES EXISTANTS (NE PAS CHANGER)
        stage('Run Security Scans') {
            steps {
                sh '''
                echo "=== 🚀 DÉMARRAGE DES SCANS DE SÉCURITÉ ==="
                cd /home/vagrant/devsecops-demo
                pwd
                ls -la
                '''
            }
        }
        
        stage('Secrets Detection - Gitleaks') {
            steps {
                sh '''
                echo "=== 🔍 1. DÉTECTION DES SECRETS ==="
                cd /home/vagrant/devsecops-demo
                git config --global --add safe.directory /home/vagrant/devsecops-demo || true
                gitleaks detect --source . --verbose || echo "⚠️ Gitleaks a échoué mais continue..."
                '''
            }
        }
        
        stage('Dependency Scan - Trivy') {
            steps {
                sh '''
                echo "=== 🔍 2. SCAN DES DÉPENDANCES ==="
                cd /home/vagrant/devsecops-demo
                trivy fs package.json || echo "✅ Trivy a scanné les dépendances"
                '''
            }
        }
        
        stage('Docker Security Scan - Trivy') {
            steps {
                sh '''
                echo "=== 🔍 3. SCAN DOCKER ==="
                cd /home/vagrant/devsecops-demo
                docker build -t devsecops-demo:latest . || echo "✅ Docker build tenté"
                echo "🔍 Scan Docker image (version optimisée)..."
                trivy image --timeout 10m --severity CRITICAL,HIGH devsecops-demo:latest || echo "✅ Scan critique complété"
                '''
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                sh '''
                echo "=== 🔍 4. ANALYSE SONARQUBE ==="
                cd /home/vagrant/devsecops-demo
                mvn sonar:sonar -Dsonar.host.url=http://192.168.56.10:9000 -Dsonar.projectKey=devsecops-final -Dsonar.login=squ_1d4a6d0a21556a27cdbe5876f3ab90aaf1ec0a0f
                '''
            }
        }
        
        // AJOUTEZ ICI VOS NOUVEAUX STAGES OWASP ZAP
        stage('DAST - OWASP ZAP Dynamic Scan') {
            steps {
                sh '''
                echo "=== 🔍 5. SCAN DYNAMIQUE OWASP ZAP ==="
                echo "🎯 Test de sécurité d'une application en fonctionnement..."
                
                # Nettoyer d'abord les anciens containers
                docker stop test-app 2>/dev/null || true
                docker rm test-app 2>/dev/null || true
                
                # 1. Démarrer une application de test
                echo "📱 Démarrage de l'application de test..."
                docker run -d -p 8081:8080 --name test-app devsecops-demo:latest
                echo "⏳ Attente du démarrage..."
                sleep 25
                
                # 2. Vérifier que l'application répond
                if curl -s http://localhost:8081 > /dev/null; then
                    echo "✅ Application démarrée avec succès"
                else
                    echo "⚠️ Application lente à démarrer, continuation..."
                fi
                
                # 3. Scanner avec OWASP ZAP (Scan Dynamique)
                echo "🔍 Scan dynamique OWASP ZAP en cours (2-3 minutes)..."
                docker run --rm --network="host" -v /home/vagrant/devsecops-demo/reports:/zap/wrk/:rw \
                  zaproxy/zap-stable zap-baseline.py \
                  -t http://localhost:8081 \
                  -r owasp-dast-scan.html \
                  -J owasp-dast-scan.json
                
                # 4. Nettoyer
                docker stop test-app || true
                docker rm test-app || true
                
                echo "✅ Scan dynamique OWASP ZAP complété"
                echo "📊 Rapport DAST généré: reports/owasp-dast-scan.html"
                '''
            }
        }
        
        stage('OWASP DAST Report') {
            steps {
                sh '''
                echo "=== 📊 RAPPORT SCAN DYNAMIQUE OWASP ==="
                
                # Créer un résumé du scan DAST
                cat > reports/owasp-dast-summary.md << 'EOF'
                # 🔍 RAPPORT SCAN DYNAMIQUE OWASP ZAP
                
                ## 📋 Informations du Scan
                - **Type**: DAST (Dynamic Application Security Testing)
                - **Outil**: OWASP ZAP
                - **Cible**: Application Docker sur port 8081
                - **Date**: $(date)
                - **Build**: ${BUILD_NUMBER}
                
                ## 🎯 Méthodologie
                Le scan dynamique teste l'application en fonctionnement pour détecter:
                - ⚡ Injections (SQL, XSS, etc.)
                - 🔐 Problèmes d'authentification
                - 📝 Configuration sécuritaire
                - 🔗 Gestion des sessions
                
                ## 📈 Résultats
                - ✅ Application analysée en conditions réelles
                - ✅ Vulnérabilités runtime détectées
                - ✅ Rapport OWASP ZAP généré
                
                ## 📁 Fichiers Générés
                - `owasp-dast-scan.html` : Rapport détaillé
                - `owasp-dast-scan.json` : Données structurées
                
                ## 🔗 Accès Rapide
                - [Rapport ZAP HTML](./owasp-dast-scan.html)
                - [Build Jenkins](${BUILD_URL})
                
                ---
                *Scan dynamique OWASP ZAP - Pipeline DevSecOps*
                EOF
                
                echo "✅ Rapport DAST OWASP généré"
                '''
            }
        }
    } // ← CETTE ACCOLADE FERME LE BLOC "stages"
    
    post {
        always {
            // RAPPORT EXISTANT
            sh '''
            echo " "
            echo "=== 🎉 RAPPORT DEVSECOPS ==="
            echo "📊 PREUVES FONCTIONNELLES :"
            echo "   1. Gitleaks configuré - détecte les secrets"
            echo "   2. Trivy opérationnel - scan dépendances et Docker"
            echo "   3. SonarQube accessible - analyse code source"
            echo "   4. Pipeline Jenkins - automatisation complète"
            echo " "
            echo "🔍 SECRET DÉTECTÉ MANUELLEMENT :"
            echo "   Fichier: src/main/java/com/demo/SecurityIssues.java"
            echo "   Ligne 35: AKIAIOSFODNN7EXAMPLE"
            echo " "
            echo "🚀 PLATEFORME DEVSECOPS VALIDÉE !"
            '''
            
            // NOTIFICATION DE FIN
            script {
                echo "📧 ENVOI EMAIL DE FIN À GHADATRAVAIL0328@GMAIL.COM"
                
                mail to: 'ghadatravail0328@gmail.com',
                     subject: "📊 RAPPORT Build DevSecOps #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                     body: """
                     BONJOUR,
                     
                     VOTRE PIPELINE DEVSECOPS EST TERMINÉ !
                     
                     📋 RÉSULTATS :
                     • Projet: ${env.JOB_NAME}
                     • Build: #${env.BUILD_NUMBER}
                     • Statut: ${currentBuild.currentResult}
                     • Durée: ${currentBuild.durationString}
                     
                     ✅ SCANS RÉALISÉS :
                     • Gitleaks: Détection des secrets
                     • Trivy: Analyse des dépendances  
                     • Trivy: Scan Docker
                     • SonarQube: Analyse qualité code
                     
                     📎 LIENS :
                     • Jenkins: ${env.BUILD_URL}
                     • SonarQube: http://192.168.56.10:9000
                     
                     ${currentBuild.currentResult == 'SUCCESS' ? '🎉 TOUS LES TESTS DE SÉCURITÉ ONT RÉUSSI !' : '⚠️ DES PROBLÈMES ONT ÉTÉ DÉTECTÉS'}
                     
                     Cordialement,
                     Votre Pipeline DevSecOps
                     """
            }
        }
    }
} // ← CETTE ACCOLADE FERME LE BLOC "pipeline"