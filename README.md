# GarageBuddy
The ultimate garage sale helper

### M6: Registration And Profile
Our goal with M6 is to create transactions and print tags


# Setup and Configuration (macOS/Linux)
We're so happy you've decided to help with the development of GarageBuddy! Before you get started, here's a list of instructions on how to get the project up and running, step-by-step.

## Install Required Dependencies
We require several dependencies for developing the GarageBuddy project. Please follow the steps below to install successfully.

### Install Homebrew/Linuxbrew
Homebrew is a package manager for OS X. Linuxbrew is a port of Homebrew for Linux machines.

**macOS:**
```bash
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

**Linux:**
```bash
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install)"
```

### Install Postgres
Postgres (PostgreSQL) is an open-source, relational, SQL database that follows the MVCC model for concurrency and is great for general-performance data tasks. It scales relatively well for small to large projects.
```bash
brew install postgres
```

Then, to run the Postgres server locally, follow the instructions `brew` gives you in the post-install message. You can always run `brew info postgres` for more details on this.

### Install Activator
Activator is the library we use to install Play's dependencies and compile the application. It's developed by Lightbend and available through `brew`.
```bash
brew install typesafe-activator
```

### Install Git
If you don't already have Git, go ahead and install it. Git is VCS (version control software) that's specifically made for branching and merging for offline users.
```bash
brew install git
```

## Configure Installation
Now that all of the dependencies are installed, we can go ahead and configure them to work with GarageBuddy.

### Clone GarageBuddy
We'll use git's clone command to download GarageBuddy to our local device. Exciting!
```bash
git clone git@github.com:deanpapastrat/GarageBuddy.git
```

### Create the Postgres Database
For Postgres, we need to create a new database to persist all of our GarageBuddy data in. Luckily, this is super easy to set up!

First, open up a Postgres console. In most cases, this should be:
```bash
psql postgres
```

This will load a Postgres console for your main Postgres database and allow you to create a new one (our desired goal). Let's go ahead and and make a database:

```sql
CREATE DATABASE garagebuddy;
```

This will create the GarageBuddy database without any username or password, so it will already work with our default install of Play. Sweet!

## Start Up the Project
Congratulations! You've installed everything. AKA, you're awesome. You should give yourself a gold star :-)

### Run Activator
Running Activator will download all the dependencies for a project - it's similar to running `sbt` in a standard Scala project.
```bash
activator
```

### Start GarageBuddy
Now that you're in the Activator console, just tell the project to run.
```bash
run
```

**WOW! You did it!** (Rainbow unicorns are jumping around and spreading magical happiness to the world)
