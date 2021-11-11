kamel run --dev --name api-simple api-simple.xml api-spec.xml \
--open-api api/openapi.json \
--resource file:api/openapi.json \
--property file:cfg/svc.properties \
--resource file:map/request.adm \
--resource file:map/response.adm