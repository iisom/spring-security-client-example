# Retrofit security into an existing application:

## Add Dependencies

Add the following dependencies to `build.gradle`

```bash 
dependencies {
	// Existing dependencies
	//...

	// Security - Add these dependencies
	implementation 'org.springframework.boot:spring-boot-starter-security'
	testImplementation 'org.springframework.security:spring-security-test'
	implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.0'
	implementation group: 'javax.xml.bind', name: 'jaxb-api', version: '2.3.1'
}
```

## Copy security and configuration

From this repository, copy the `configuration` and the `security` packages to the base of your application's code.  You will need to copy these packages one at a time from Windows Explorer, directly into IntelliJ.  NOTE: IntelliJ will refactor everything per your application's setup.

## Environment Variable `security.jwt.secret`

Add the `security.jwt.secret` to your environment variables in your run configuration.  NOTE: The value should be the same as the identity provider you are associating with.  If you are working in a Kubernetes cluster, there should be a secret named something like `jwt-key-secret`.

```bash
## Print out the value of the secret
kubectl get secrets jwt-key-secret -o yaml
apiVersion: v1
data:
  JWT_SECRET_KEY: R1lCVi1YSUNNLVFFU0UtUFVXWC1CT1pLLVdYVlU=
kind: Secret
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"v1","data":{"JWT_SECRET_KEY":"R1lCVi1YSUNNLVFFU0UtUFVXWC1CT1pLLVdYVlU="},"kind":"Secret","metadata":{"annotations":{},"name":"jwt-key-secret","namespace":"default"},"type":"Opaque"}
  creationTimestamp: "2024-07-31T20:29:44Z"
  name: jwt-key-secret
  namespace: default
  resourceVersion: "650470"
  uid: 598ff3e9-fef5-4e3e-90dc-a164272f4b78
type: Opaque

### Decode the value of key JWT_SECRET_KEY
echo -n "R1lCVi1YSUNNLVFFU0UtUFVXWC1CT1pLLVdYVlU=" | base64 -d
GYBV-XICM-QESE-PUWX-BOZK-WXVU% 

## Add the decoded value to your environment
export security.jwt.secret=GYBV-XICM-QESE-PUWX-BOZK-WXVU

```

- Your values may be different.  It is important that the value used by the Identity Provider match that of the client.
