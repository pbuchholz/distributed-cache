FROM openjdk

ADD payara41 /usr/lib/payara41
COPY passwordfile /var/passwordfile 

# Set up environment 
ENV ADMIN_USER admin
ENV DOMAIN_NAME domain1 

# Update and install packages
RUN apt-get update

RUN /usr/lib/payara41/glassfish/bin/asadmin --user ${ADMIN_USER} --passwordfile /var/passwordfile --interactive=false change-admin-password --domain_name ${DOMAIN_NAME}

RUN sed -i.bak 's/^\(AS_ADMIN_PASSWORD=\).*/\1admin/' /var/passwordfile

RUN /usr/lib/payara41/glassfish/bin/asadmin start-domain ${DOMAIN_NAME} && \
	/usr/lib/payara41/glassfish/bin/asadmin --user ${ADMIN_USER} --passwordfile /var/passwordfile enable-secure-admin && \
	/usr/lib/payara41/glassfish/bin/asadmin stop-domain ${DOMAIN_NAME}   

CMD ["/usr/lib/payara41/glassfish/bin/startserv"]

EXPOSE 4848
