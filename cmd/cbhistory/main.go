package main

import (
	cbh "github.com/wenerme/cbhistory"
)

func main() {
	s := cbh.NewServer(cbh.LoadConfig("config.toml"))
	err := s.Start()
	if err != nil {
		panic(err)
	}
	select {}
}
