# k8s-status
Simple Red-Yellow-Green Status based on k8s Deployments status

# Docker
https://hub.docker.com/r/hangst/k8s-status

# Call   
    
    
     $ curl -v  http://my-host/status/simple
     *   Trying ::1...
     * TCP_NODELAY set
     * Connected to localhost (::1) port 8080 (#0)
     > GET /simple HTTP/1.1
     > Host: localhost:8080
     > User-Agent: curl/7.54.0
     > Accept: */*
     >
     < HTTP/1.1 200
     < Content-Type: text/plain;charset=UTF-8
     < Content-Length: 5
     < Date: Fri, 22 Mar 2019 13:20:34 GMT
     <
     * Connection #0 to host localhost left intact
     green     
