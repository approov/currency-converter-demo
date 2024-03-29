ARG TAG=3.10-slim
FROM python:${TAG}

ARG CONTAINER_USER="python"
ARG CONTAINER_UID="1000"
ARG ZSH_THEME="robbyrussell"

# Will not prompt for questions
ENV DEBIAN_FRONTEND=noninteractive \
    CONTAINER_USER=python \
    CONTAINER_UID=1000 \
    CONTAINER_HOME=/home/${CONTAINER_USER}

RUN apt update && \
    apt -y upgrade && \
    apt -y install \
      ca-certificates \
      locales \
      tzdata \
      inotify-tools \
      libnss3-tools \
      curl \
      git \
      zsh \
      unzip \
      libxss1 \
      python3-pip && \

    locale-gen en_GB.UTF-8 && \
    dpkg-reconfigure locales && \

    #https://github.com/guard/listen/wiki/Increasing-the-amount-of-inotify-watchers
    printf "fs.inotify.max_user_watches=524288\n" >> /etc/sysctl.conf && \

    useradd -m -u ${CONTAINER_UID} -s /bin/bash ${CONTAINER_USER} && \

    # Install Oh My Zsh for Root and Node user
    bash -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)" && \
    chsh -s /usr/bin/zsh && \
    cp -R /root/.oh-my-zsh /home/"${CONTAINER_USER}" && \
    cp /root/.zsh* /home/"${CONTAINER_USER}" && \
    sed -i "s/\/root/\/home\/${CONTAINER_USER}/g" /home/"${CONTAINER_USER}"/.zshrc && \
    chown -R "${CONTAINER_USER}":"${CONTAINER_USER}" /home/"${CONTAINER_USER}" && \
    sed -i s/ZSH_THEME=\"robbyrussell\"/ZSH_THEME=\"${ZSH_THEME}\"/g /home/${CONTAINER_USER}/.zshrc

ENV LANG=en_GB.UTF-8 \
    LANGUAGE=en_GB:en \
    LC_ALL=en_GB.UTF-8

USER ${CONTAINER_USER}

RUN pip3 install \
      flask \
      requests \
      python-dotenv \
      Babel \
      pyjwt \
      ring

# pip install will put the executables under ~/.local/bin
ENV PATH=/home/"${CONTAINER_USER}"/.local/bin:$PATH

WORKDIR /home/${CONTAINER_USER}/workspace

CMD ["zsh"]
