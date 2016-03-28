package cbhistory

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/codegangsta/negroni"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"strings"
)

type httpServer struct {
	s Server
}

func newHttpServer(s Server) *httpServer {
	return &httpServer{s: s}
}
func (s *httpServer) Start() error {
	router := mux.NewRouter()
	router.HandleFunc("/cmt", s.cmtHandler)

	n := negroni.Classic()
	// Or use a middleware with the Use() function
	//	n.Use(Middleware3)
	// router goes last
	n.UseHandler(router)
	log.Info("Start http server at %v", s.s.Config().Http.Address)
	go n.Run(s.s.Config().Http.Address)
	return nil
}
func (s *httpServer) cmtHandler(rw http.ResponseWriter, r *http.Request) {
	r.ParseForm()
	op := r.FormValue("op")
	log.Debug("cmt?op=%v", op)
	o, err := parseOp(op)
	response := cmtBusy
	if err == nil && !o.Valid() {
		err = errors.New(fmt.Sprintf("Invalid op %v", op))
		response.Message = "Invalid op"
	}
	if err == nil {
		var cmt CmtResponse
		_, err = s.s.Repo().FindById(o.Sid, &cmt)
		if err == nil {
			response = cmt
		}
	}
	if err != nil {
		log.Info("Faield %v cmt?op=%v", err)
	}
	bytes, err := json.Marshal(response)
	if err != nil {
		log.Info("Faield json.Marshal %v", err)
		bytes, err = json.Marshal(cmtBusy)
		if err != nil {
			panic(err)
		}
	}
	rw.Header().Add("Content-Type", "application/json")
	rw.Write(bytes)
}

type op struct {
	Page int
	Sid  int
	Sn   string
}

func (o op) String() string {
	return fmt.Sprintf("%v,%v,%v", o.Page, o.Sid, o.Sn)
}
func parseOp(s string) (o op, err error) {
	split := strings.Split(s, ",")
	if len(split) != 3 {
		err = errors.New("Wrong op format")
		return
	}
	o.Page, err = strconv.Atoi(split[0])
	if err != nil {
		err = errors.New(fmt.Sprint("Wrong page number '%v'", split[0]))
		return
	}
	o.Sid, err = strconv.Atoi(split[1])
	if err != nil {
		err = errors.New(fmt.Sprint("Wrong sid number '%v'", split[1]))
		return
	}
	o.Sn = split[2]
	return
}
func (o op) Valid() bool {
	return o.Page > 0 && o.Sid > 0 && len(o.Sn) > 0
}
