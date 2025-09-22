-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 22/09/2025 às 19:47
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `newpix`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `sessoes`
--

CREATE TABLE `sessoes` (
  `token` varchar(255) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `criado_em` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `sessoes`
--

INSERT INTO `sessoes` (`token`, `usuario_id`, `criado_em`) VALUES
('16faf1cd-26f8-4802-b97a-f18702878493', 1, '2025-09-15 03:39:08'),
('2bd28890-1d7c-446f-a49c-225f9c89d435', 1, '2025-09-14 16:23:11'),
('3962ce5e-8453-4737-bad5-8ac5ffa7490b', 1, '2025-09-15 01:50:29'),
('4c0332d6-87ee-4c57-b861-e9ee23f02a45', 1, '2025-09-14 17:03:46'),
('4e3ef1e8-d775-4229-95e4-9051e73b49fe', 1, '2025-09-15 04:38:09'),
('4f1ac976-c53d-43db-b083-8b753f92c8c4', 2, '2025-09-14 17:15:34'),
('51eb488d-eace-44df-b574-2de34d476ae1', 1, '2025-09-14 17:05:20'),
('5ba1179f-6500-41ec-be15-cdcc66424ff0', 1, '2025-09-14 22:52:08'),
('6f0fccac-dc91-4e63-a53a-e25d81a4de48', 1, '2025-09-14 22:40:34'),
('7328d482-089f-4a1b-91b2-cf484ccc6486', 3, '2025-09-14 17:07:45'),
('7a44fff6-d52c-4baa-8542-67009166de93', 1, '2025-09-14 17:15:24'),
('898f26f6-ea7b-48ea-bff1-9c00bb8eb160', 2, '2025-09-14 17:23:37'),
('a1a94716-973d-4800-a34c-7365d2655e6a', 1, '2025-09-14 16:18:38'),
('a1ee2201-4750-4d24-afc0-61dc6ce6c4a3', 1, '2025-09-14 16:27:20'),
('caaa7e69-f1cd-4e40-98fe-40f49c72db4c', 1, '2025-09-14 17:23:44'),
('cb3327ee-8d07-4aa7-86a1-724ae6e474eb', 3, '2025-09-14 17:15:48'),
('ccb45283-4dcc-45bd-9d46-2c38672b1952', 2, '2025-09-14 17:06:45'),
('df65d160-14a0-455c-a17c-50464ba34771', 1, '2025-09-14 16:37:31'),
('f37e9806-f68d-4b56-b2b9-4abb73440faf', 1, '2025-09-14 23:28:07');

-- --------------------------------------------------------

--
-- Estrutura para tabela `transacoes`
--

CREATE TABLE `transacoes` (
  `id` int(11) NOT NULL,
  `id_remetente` int(11) NOT NULL,
  `id_destinatario` int(11) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `transacoes`
--

INSERT INTO `transacoes` (`id`, `id_remetente`, `id_destinatario`, `valor`, `data`) VALUES
(1, 1, 2, 1.00, '2025-09-14 23:28:14'),
(2, 2, 1, 123.00, '2025-09-15 02:21:11'),
(3, 2, 1, 11.00, '2025-09-15 02:21:35'),
(4, 2, 1, 1001.00, '2025-09-15 02:21:57'),
(5, 1, 2, 1.00, '2025-09-15 02:22:05'),
(6, 1, 2, 12312.00, '2025-09-15 03:50:51'),
(7, 1, 2, 300000.00, '2025-09-15 03:51:15'),
(8, 2, 1, 100000.00, '2025-09-15 03:52:05'),
(9, 2, 1, 1.00, '2025-09-15 04:00:40'),
(10, 1, 2, 123123.00, '2025-09-15 04:38:54'),
(11, 1, 2, 123.12, '2025-09-15 04:39:05'),
(12, 1, 2, 105.40, '2025-09-15 05:00:57'),
(13, 1, 2, 150.12, '2025-09-15 05:01:36'),
(14, 1, 2, 1.00, '2025-09-15 05:17:47'),
(15, 1, 2, 13230.00, '2025-09-15 05:17:54'),
(16, 1, 2, 23320.00, '2025-09-15 05:46:30');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nome` varchar(120) NOT NULL,
  `cpf` varchar(14) NOT NULL,
  `senha` varchar(120) NOT NULL,
  `saldo` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuarios`
--

INSERT INTO `usuarios` (`id`, `nome`, `cpf`, `senha`, `saldo`) VALUES
(1, 'Lucas Gonçalves Padilha', '123.123.123-00', '123123', 85658.59),
(2, 'Gabrielly', '123.123.123-01', '123123', 372587.64),
(3, 'Sebastiao', '123.123.123-02', '123123', 412.00);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `sessoes`
--
ALTER TABLE `sessoes`
  ADD PRIMARY KEY (`token`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Índices de tabela `transacoes`
--
ALTER TABLE `transacoes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_remetente` (`id_remetente`),
  ADD KEY `id_destinatario` (`id_destinatario`);

--
-- Índices de tabela `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cpf` (`cpf`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `transacoes`
--
ALTER TABLE `transacoes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de tabela `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `sessoes`
--
ALTER TABLE `sessoes`
  ADD CONSTRAINT `sessoes_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `transacoes`
--
ALTER TABLE `transacoes`
  ADD CONSTRAINT `transacoes_ibfk_1` FOREIGN KEY (`id_remetente`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `transacoes_ibfk_2` FOREIGN KEY (`id_destinatario`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
