FROM machines/filestash@sha256:2f69ce7814006512b6bbcd8aef4767c5424d270ce0b12819874e7dbd4e3de2b9
USER root
# Intel's full VA-API driver is needed for hardware encoding on HD Graphics 530.
RUN sed -i -e 's/^Suites: stable/Suites: trixie/' -e 's/ stable/ trixie/g' -e 's/^Components: main$/Components: main non-free/' /etc/apt/sources.list.d/debian.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends intel-media-va-driver-non-free vainfo \
    && rm -rf /var/lib/apt/lists/*
ENV LIBVA_DRIVER_NAME=iHD
USER filestash
