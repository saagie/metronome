package dcos.metronome.model

import mesosphere.marathon.state.Container.Docker.PortMapping
import mesosphere.marathon.state.{Parameter}
import org.apache.mesos.Protos.ContainerInfo.DockerInfo.Network

import scala.collection.immutable.Seq

trait Container

case class DockerSpec(image: String, network: Option[Network] = None, portMappings: Option[Seq[mesosphere.marathon.state.Container.Docker.PortMapping]] = None, privileged: Boolean= false, parameters: Seq[Parameter] = Nil, forcePullImage: Boolean = false) extends Container

object DockerSpec {
  val DefaultImage = ""
  val DefaultNetwork = None
  val portMappings = None
  val privileged = None
  val parameters = None
  val forcePullImage = None

  def withDefaultPortMappings(
                               image: String = "",
                               network: Option[Network] = None,
                               portMappings: Option[Seq[mesosphere.marathon.state.Container.Docker.PortMapping]] = None,
                               privileged: Boolean = false,
                               parameters: Seq[Parameter] = Nil,
                               forcePullImage: Boolean = false): DockerSpec = DockerSpec(
    image = image,
    network = network,
    portMappings = network match {
      case Some(networkMode) if networkMode == Network.BRIDGE =>
        portMappings.map(_.map {
          // backwards compat: when in BRIDGE mode, missing host ports default to zero
          case PortMapping(x, None, y, z, w, a) => PortMapping(x, Some(mesosphere.marathon.state.Container.Docker.PortMapping.HostPortDefault), y, z, w, a)
          case m => m
        })
      case _ => portMappings
    },
    privileged = privileged,
    parameters = parameters,
    forcePullImage = forcePullImage)
}