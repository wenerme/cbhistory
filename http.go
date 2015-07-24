package cbhistory

import (
	"github.com/codegangsta/negroni"
	"github.com/gorilla/mux"
	"net/http"
)

type httpServer struct {
	s Server
}

func newHttpServer(s Server) *httpServer {
	return nil
}
func (s *httpServer) Start() error {
	router := mux.NewRouter()
	router.HandleFunc("/cmt", s.cmtHandler)

	n := negroni.Classic()
	// Or use a middleware with the Use() function
	//	n.Use(Middleware3)
	// router goes last
	n.UseHandler(router)
	n.Run(s.s.Config().Http.Address)

	return nil
}
func (s *httpServer) cmtHandler(rw http.ResponseWriter, r *http.Request) {

}
