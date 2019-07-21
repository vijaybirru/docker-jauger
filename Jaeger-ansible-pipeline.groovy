/***************************************************************
    Title: Jaeger-ansible-pipeline.groovy.

    Description:
    This groovy script performs this operation:
    1) Fetch the nodes as mentioned in the environment yml file.
    2) Install Docker.
    2) Install Jaeger.

    Author:

*************************************************************/

    	node {

      stage("Docker install ") {

      sh """
      rm -rf nodes
      echo "[NODE]" > nodes
      for m in ${ipAddress}; do
         echo "\$m ansible_user=ec2-user" >> nodes
      done
      cat nodes

      """
          sshagent(['Jauger']) {
          sh script: """
              export ANSIBLE_HOST_KEY_CHECKING=False
              cd /var/lib/jenkins/workspace/jaugaur/docker-jauger
              ansible-playbook docker_install_ansible.yml -s -e host_key_checking=False  -i ./nodes -v
          """

          }
      }
    stage("Install Jaeger") {

        sshagent(["${credentials}"]) {
            sh script: """

                export ANSIBLE_HOST_KEY_CHECKING=False
                cd /var/lib/jenkins/workspace/jaugaur/docker-jauger
                ansible-playbook ec2_yum_update_ansible.yml -s -e host_key_checking=False -i ./nodes -v
                ansible-playbook Jaeger_install_ansible.yml -s -e host_key_checking=False -i ./nodes -v

            """

        }
    }

}
