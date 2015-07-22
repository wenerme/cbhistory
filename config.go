package cbhistory

import (
	"github.com/BurntSushi/toml"
	"io/ioutil"
	"os"
	"time"
)

type Duration struct {
	time.Duration
}

func (d *Duration) UnmarshalText(text []byte) error {
	var err error
	d.Duration, err = time.ParseDuration(string(text))
	return err
}

type Config struct {
	Collector struct {
		Enable bool
		Jobs   []struct {
			Description string
			Expression  string
			Type        string
			Interval    Duration
			Delay       Duration
		}
	}
	Http struct {
		Enable bool
		Port   int
	}
	Database struct {
		Type     string
		File     string // Used for sqlite
		Host     string
		Schema   string
		User     string
		Password string
	}
}

func LoadConfig(fn string) *Config {
	f, err := os.Open(fn)
	if err != nil {
		panic(err)
	}
	defer f.Close()
	buf, err := ioutil.ReadAll(f)
	if err != nil {
		panic(err)
	}
	var config Config
	if err := toml.Unmarshal(buf, &config); err != nil {
		panic(err)
	}
	return &config
}
