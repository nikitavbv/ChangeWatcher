==============
Change Watcher
==============

|Build Status| |Docker Build| |codecov.io| |license-mit|

=====

Monitor webpage changes (pixel control is used) and get notified.

=====

======
Usage:
======

For simplicity, you can use docker-compose to start application container and PostgreSQL with default configuration and frontend::

    wget https://raw.githubusercontent.com/nikitavbv/ChangeWatcher/master/docker-compose.yaml
    docker-compose up --build -d

The app will be listening on port 80.

To stop::

    docker-compose down

Alternatively, you can use individual container (this requires linking to database container)::

    docker run --name changewatcher -p 80:8080 --link db:db --env DB_URL=jdbc:postgresql://db:3306/changewatcher --env DB_USERNAME=user --env DB_PASSWORD=password -d nikitavbv/changewatcher


(Database URL and credentials are passed via environmental variables)

.. |Build Status| image:: https://img.shields.io/travis/nikitavbv/ChangeWatcher/master.svg?label=Build%20status
   :target: https://travis-ci.org/nikitavbv/ChangeWatcher
.. |Docker Build| image:: https://img.shields.io/docker/build/nikitavbv/changewatcher.svg
   :target: https://hub.docker.com/r/nikitavbv/changewatcher
.. |codecov.io| image:: https://img.shields.io/codecov/c/github/nikitavbv/ChangeWatcher/master.svg?label=coverage
   :target: https://codecov.io/github/nikitavbv/ChangeWatcher?branch=master
.. |license-mit| image:: https://img.shields.io/badge/License-MIT-yellow.svg
   :target: https://opensource.org/licenses/MIT
