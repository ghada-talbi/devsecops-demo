FROM nginx:1.18

# Image avec vulnérabilités connues
USER root

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
